package org.vietspider.link.regex;

public class Element {
  
  public static enum ElementType {
    HOST,
    WWW,
    ELEMENT,
    PARAM,
    REFERENCE,
    FUNCTION,
  }

	private String value;
	private ElementType type;
	private ElementType subType;
	private Symbols symbols;

	public Element(String value, ElementType type) {
		this.value = value;
		this.type = type;
//		System.out.println(" value " + value + " type "+ type);
		if(type == ElementType.HOST
		    && value.startsWith("www")) {
		  this.subType = ElementType.WWW;
		}
//		System.out.println(" value " + value + " type "+ type);
	}

	public String getValue() { return value; }
	public void setValue(String value) { this.value = value; }

	public Symbols getSymbols() {
		if(symbols == null) {
			symbols = new Symbols(this.value);
		}
		return symbols;
	}

	public ElementType getType() { return type; }
	
	public ElementType getSubType() { return subType; }

	public boolean match(Element ele) {
//	  System.out.println(type + " : "+ value + " : "+ ele.value);
		if(type != ele.type) return false;
		if(subType == ElementType.WWW
		    && subType == ele.subType ) return true;
		boolean _return = getSymbols().match(ele.getValue());
//		if(!_return) {
//		  System.out.println("symbols: " + getSymbols());
//		  System.out.println("value:   " + ele.getValue());
//		}
		return _return;
//		return getSymbols().match(ele.getValue());
	}

		
}

