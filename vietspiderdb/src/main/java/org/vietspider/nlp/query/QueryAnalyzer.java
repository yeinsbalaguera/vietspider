/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vietspider.bean.NLPData;
import org.vietspider.db.DataSimpleCache;
import org.vietspider.nlp.INlpExtractor;
import org.vietspider.nlp.INlpFilter;
import org.vietspider.nlp.INlpNormalize;
import org.vietspider.nlp.impl.Address;
import org.vietspider.nlp.impl.AddressNormalization;
import org.vietspider.nlp.impl.Addresses;
import org.vietspider.nlp.impl.AreaNormalization;
import org.vietspider.nlp.impl.Place;
import org.vietspider.nlp.impl.Price;
import org.vietspider.nlp.impl.PriceExtractor;
import org.vietspider.nlp.impl.PriceNormalization;
import org.vietspider.nlp.impl.Unit;
import org.vietspider.nlp.impl.ao.NlpAction;
import org.vietspider.nlp.impl.ao.NlpObject;
import org.vietspider.nlp.impl.ao.SimpleActionExtractor;
import org.vietspider.nlp.impl.ao.SimpleObjectExtractor;
import org.vietspider.nlp.impl.area.AreaExtractor;
import org.vietspider.nlp.impl.area.AreaFilter;
import org.vietspider.nlp.query.ValueRange.DoubleValueRange;
import org.vietspider.nlp.query.ValueRange.FloatValueRange;
import org.vietspider.nlp.text.TextElement;
import org.vietspider.nlp.text.TextElement.Point;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class QueryAnalyzer {

  private final static QueryAnalyzer PROCESSOR = new QueryAnalyzer();

  public final static QueryAnalyzer getProcessor() { return PROCESSOR; }

  private INlpFilter [] filters;
  private INlpExtractor<?> [] extractors;
  private INlpNormalize [] normalizes;
  
  private SimpleActionExtractor actionExtractor;
  private SimpleObjectExtractor objectExtractor;

  private DataSimpleCache<HashMap<Short, Collection<?>>>  cached;

  private QueryAnalyzer() {
    cached = new DataSimpleCache<HashMap<Short, Collection<?>>>(15*60l);
    filters = new INlpFilter[] {
        //        new EmailFilter(), new PhoneFilter(), 
        new AreaFilter(), new QueryPriceFilter()
        //        new PriceFilter()/*, new ActionObjectFilter()*/
    };

    extractors = new INlpExtractor[] {
        //        new EmailExtractor(), new PhoneExtractor(), 
        new AreaExtractor(), new PriceExtractor(),
        /*new PriceExtractor(), */new QActionObjectExtractor()
    };

    normalizes = new INlpNormalize[] {
        new AreaNormalization() {
          @SuppressWarnings("all")
          public void normalize(String id, HashMap<Short, Collection<?>> map) {
            List<Unit> list = (List<Unit>) map.get(NLPData.AREA);
            if(list == null) return;

            if(!list.isEmpty()) return;
            map.put(NLPData.AREA, null);
            return;
          }  
        },
        new AddressNormalization(), 
        new PriceNormalization() {
          protected void putMap(HashMap<Short, Collection<?>> map, List<Price> prices) {
            map.put(NLPData.PRICE, prices);
          }
        }
    };
    
    this.actionExtractor = new SimpleActionExtractor();
    this.objectExtractor = new SimpleObjectExtractor();
  }

  public Map<Short, Collection<?>> process(String text) {
    text = text != null ? text.trim() : "";
    HashMap<Short, Collection<?>> map = cached.get(text);
    if(map != null) return map;
    //    StringBuilder builder = new StringBuilder();
    //    for(int i = 0; i < text.length(); i++) {
    //      char c = text.charAt(i);
    //      if(Character.isLetterOrDigit(c)) {
    //        builder.append(c);
    //      } else if (Character.isWhitespace(c) 
    //          || Character.isSpaceChar(c)){
    //        builder.append(c);
    //      }
    //    }
    //    text = builder.toString();

    map = new HashMap<Short, Collection<?>>();
    TextElement element = new TextElement(text);
    for(int i = 0; i < filters.length; i++) {
      filters[i].filter("query", element);
    }

    for(int i = 0; i < extractors.length; i++) {
      //        System.out.println(extractors[i].isExtract(element) + "=" + element.getValue()  );
      if(!extractors[i].isExtract(element)) continue;
      Collection<?> collection = map.get(extractors[i].type());
      if(collection == null) {
        collection = extractors[i].createCollection();
        map.put(extractors[i].type(), collection);
      }
      extractors[i].extract("query", collection, element);
      //      System.out.println(extractors[i].type() + " : "+ collection.size());
    }

    Addresses addresses = QAddressDetector.getInstance().detectAddresses(element);
    if(addresses != null) {
      List<String> list = collectAddress(addresses);
      if(list.size() > 0) map.put(NLPData.ADDRESS, list);

      list = collectCities(addresses);
      if(list.size() > 0) map.put(NLPData.CITY, list);
    }

    for(int i = 0; i < extractors.length; i++) {
      Collection<?> collection = map.get(extractors[i].type());
      if(collection == null) continue;
      extractors[i].closeCollection(collection);
    }

    List<Point> points = element.getPoints();

    //chinh chu
    int index = text.indexOf("chính chủ");
    if(index < 0) index = text.indexOf("chinh chu");
    if(index > -1) {
      Point point = new Point(1, index, index + 9);
      points.add(point);
      map.put(NLPData.OWNER, Arrays.asList("true"));
    }


    Collections.sort(points, new Comparator<Point>() {
      public int compare(Point p1, Point p2) {
        int v = p1.getStart() - p2.getStart();
        if(v != 0) return v;
        return p1.getEnd() - p2.getEnd();
      }
    });
    mergePoints(points);

    List<String> list = new ArrayList<String>();
    int start = 0;
    for(int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      int end = point.getStart();
      if(end == start) {
        start = point.getEnd();
        continue;
      }

      //      System.out.println("bebe " + text.substring(point.getStart(), point.getEnd()));

      if(end == start+1 && 
          (Character.isWhitespace(text.charAt(start))
              || Character.isSpaceChar(text.charAt(start)))) {
        start = point.getEnd();
        continue;
      }
      //      System.out.println(" truoc do " + text.substring(0, start));
      addText(list, text.substring(start, end).trim());
      //      System.out.println("=====>" + text.substring(start, end));
      start = point.getEnd();
    }

    if(start < text.length()) {
      //      System.out.println(" hihi "+ value);
      addText(list, text.substring(start, text.length()).trim());

      //      System.out.println("=====>"+ text.substring(start, text.length()));
    }

    map.put(NLPData.NORMAL_TEXT, list);

    for(int i = 0; i < normalizes.length; i++) {
      normalizes[i].normalize("query", map);
    }

    //    System.out.println(map.get(NLPData.AREA).size());

    //    StringBuilder builder = new StringBuilder(text);
    //    for(int i = 0; i < points.size(); i++) {
    //      Point point = points.get(i);
    //      for(int j = point.getStart(); j < point.getEnd(); j++) {
    //        builder.setCharAt(j, '#');
    //      }
    //      System.out.println(point.getStart() + " : "+ point.getEnd());
    //    }
    //    String text2 = builder.toString().trim();
    //    System.out.println("=== >|" + builder + "|");
    List<ValueRange<?>> ranges = new ArrayList<ValueRange<?>>();
    map.put(ValueRange.RANGE, ranges);

    createPriceRanges(map);
    createAreaRange(map);

    cached.put(text, map);
    
    Collection<?> collection = map.get(NLPData.ACTION_OBJECT);
    if(collection == null || collection.size() < 1) {
      NlpObject object = objectExtractor.extract(element);
      if(object != null) {
        List<NlpObject> objects = new ArrayList<NlpObject>();
        objects.add(object);
        map.put(NLPData.OBJECT, objects);
      }
      
      NlpAction action = actionExtractor.extract(element);
      if(action != null) {
        List<NlpAction> actions = new ArrayList<NlpAction>();
        actions.add(action);
        map.put(NLPData.ACTION, actions);
      }
    }

    return map;
  }

  private void addText(List<String> list, String value) {
    value = value.trim();
    if(value.length() < 1) return;
    int index = 0;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isLetterOrDigit(c)) {
        list.add(value);
        return;
      }
      index++;
    }

  }

  private static void mergePoints(List<Point> points) {
    for(int i = 0; i < points.size() -1; i++) {
      Point p1  = points.get(i);
      Point p2  = points.get(i+1);
      if(p2.getStart() == p1.getStart()) {
        points.remove(i);
        mergePoints(points);
        return;
      }

      if(p2.getStart() < p1.getEnd()) {
        int s = p1.getStart();
        int e  = p2.getEnd() > p1.getEnd() ? p2.getEnd() : p1.getEnd();
        Point p = new Point(p1.getScore(), s, e);
        points.remove(i);
        points.set(i, p);
        mergePoints(points);
        return;
      }
    }
  }

  private List<String> collectAddress(Addresses addresses) {
    List<String> list = new ArrayList<String>();
    List<Address> children = addresses.list();
    for(int i = 0; i < children.size(); i++) {
      collectAddress(list, children.get(i));
    }
    return list;
  }

  private List<String> collectCities(Addresses addresses) {
    List<String> list = new ArrayList<String>();
    List<Address> children = addresses.list();
    for(int i = 0; i < children.size(); i++) {
      Point point = children.get(i).getPoint();
      if(point.getStart() != point.getEnd()) {
        list.add(children.get(i).getPlace().getName());
      }
    }
    return list;
  }

  private void collectAddress(List<String> list, Address address) {
    Point point = address.getPoint();
    if(point.getStart() != point.getEnd()
        && address.getPlace().getLevel() != Place.CITY) {
      list.add(address.getPlace().getName());
    }

    List<Address> children = address.getChildren();
    for(int i = 0; i < children.size(); i++) {
      collectAddress(list, children.get(i));
    }
  }

  @SuppressWarnings("unchecked")
  private void createPriceRanges(HashMap<Short, Collection<?>> map) {
    Collection<?> collections = map.get(NLPData.PRICE);
    if(collections == null || collections.isEmpty()) return ;
    
    List<Double> priceTotal = new ArrayList<Double>();
    List<Double> priceUnitM2 = new ArrayList<Double>();
    List<Double> priceMonth = new ArrayList<Double>();

    Iterator<?> iterator = collections.iterator();
    while(iterator.hasNext()) {
      Price price = (Price) iterator.next();
      if(price.getUnit() == Price.UNIT_TOTAL) {
        priceTotal.add(price.getValue());
      } else if(price.getUnit() == Price.UNIT_M2) {
        priceUnitM2.add(price.getValue());
      } else if(price.getUnit() == Price.UNIT_MONTH) {
        priceMonth.add(price.getValue());
      } else {
        price.setUnit(Price.UNIT_TOTAL);
        priceTotal.add(price.getValue()); 
      }
    }
    

    List<ValueRange<?>> ranges = (List<ValueRange<?>>)map.get(ValueRange.RANGE);
    createPriceRanges(ranges, ValueRange.PRICE_TOTAL_RANGE, priceTotal, 50*1000d*1000d);
    createPriceRanges(ranges, ValueRange.PRICE_M2_RANGE, priceUnitM2, 3*1000d*1000d);
    createPriceRanges(ranges, ValueRange.PRICE_MONTH_RANGE, priceMonth, 0.5*1000d*1000d);
  }

  @SuppressWarnings("unchecked")
  private void createAreaRange(Map<Short, Collection<?>> map) {
    Collection<?> collections = map.get(NLPData.AREA);
    if(collections == null || collections.isEmpty()) return;
    
    List<ValueRange<?>> ranges = (List<ValueRange<?>>)map.get(ValueRange.RANGE);
    
    FloatValueRange range = null;
    for(int i = 0; i < ranges.size(); i++) {
      if(ranges.get(i).getType() != ValueRange.AREA_RANGE) continue;
      range = (FloatValueRange)ranges.get(i);
      break;
    }
    if(range == null) {
      range = new FloatValueRange(ValueRange.AREA_RANGE);
      ranges.add(range);
    }

    Iterator<?> iterator = collections.iterator();
    if(!iterator.hasNext()) return;
    Unit unit = (Unit)iterator.next();
    float min = 0;
    float max = 0;
    if(unit.getNext() != null 
        && unit.getNext().getNext() == null) {
      min = unit.getValue();
      max = unit.getNext().getValue();
      range.addRange(min, max);
      ranges.add(range);
      return;
    }

    while(unit != null) {
      if(unit.getValue() >= 20) {
        min = unit.getValue() - 3;
        max = unit.getValue() + 3;
      } else {
        min = unit.getValue() - 1;
        max = unit.getValue() + 1;
      }
      range.addRange(min, max);
      ranges.add(range);
      unit = unit.getNext();
    }
  }
  
  @SuppressWarnings("unchecked")
  public <T extends ValueRange<?>> T getRange(Map<Short, Collection<?>> map, int rangeType) {
    List<ValueRange<?>> ranges = (List<ValueRange<?>>)map.get(ValueRange.RANGE);
    if(ranges == null) return null;
    for(int i = 0; i < ranges.size(); i++) {
      if(ranges.get(i).getType() != rangeType) continue;
      return (T)ranges.get(i);
    }
    return null;
  }

  private void createPriceRanges(List<ValueRange<?>> ranges, 
      short rangeType, List<Double> values, double rate)  {
    
    DoubleValueRange range = null;
    for(int i = 0; i < ranges.size(); i++) {
      if(ranges.get(i).getType() != rangeType) continue;
      range = (DoubleValueRange)ranges.get(i);
      break;
    }
    if(range == null) {
      range = new DoubleValueRange(rangeType);
      ranges.add(range);
    }
    
    if(values.size() == 2) {
      double min = values.get(0);
      double max = values.get(1);
      range.addRange(min, max);
      ranges.add(range);
      return ;
    } 

    for(int i = 0; i < values.size(); i++) {
      double min = values.get(i) - rate;
      double max = values.get(i) + rate;

      range.addRange(min, max);
      ranges.add(range);
    }
  }
  
  public static void main(String[] args) {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(-1, 4, 7));
    points.add(new Point(-1, 4, 8));
    points.add(new Point(-1, 9, 15));
    points.add(new Point(-1, 30, 37));
    points.add(new Point(-1, 17, 25));
    points.add(new Point(-1, 19, 29));
    points.add(new Point(-1, 31, 49));
    points.add(new Point(-1, 35, 45));

    Collections.sort(points, new Comparator<Point>() {
      public int compare(Point p1, Point p2) {
        int v = p1.getStart() - p2.getStart();
        if(v != 0) return v;
        return p1.getEnd() - p2.getEnd();
      }
    });

    mergePoints(points);

    for(int i = 0; i < points.size(); i++) {
      Point point = points.get(i);
      System.out.println(point.getStart() + " : "+ point.getEnd());
    }
  }


}
