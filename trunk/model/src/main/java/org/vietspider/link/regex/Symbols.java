package org.vietspider.link.regex;

public class Symbols {

  private Node root;

  public Symbols(String value) {
    int index = 0;
    int start = 0;
    Node current = null;
    while(index < value.length()) {
      char c = value.charAt(index);
      if(c == '*') {
        String text = value.substring(start, index);
        if(text.length() > 0) {
          current = create(current, text, NodeType.CONSTANT);
        }
        current = create(current, "*", NodeType.ANY);
        start = index+1;
      } else if(c == '@') {
        String text = value.substring(start, index);
        if(text.length() > 0) {
          current = create(current, text, NodeType.CONSTANT);
        }
        current = create(current, "@", NodeType.LETTER);
        start = index+1;
      } else  if(c == '$') {
        String text = value.substring(start, index);
        if(text.length() > 0) {
          current = create(current, text, NodeType.CONSTANT);
        }
        current = create(current, "$", NodeType.DIGIT);
        start = index+1;
      }
      index++;
    }
    if(start >= value.length()) return;
    String text = value.substring(start);
    current = create(current, text, NodeType.CONSTANT);
  }

  private Node create(Node current, String text, NodeType type) {
    Node symbol = new Node(text, type);
    if(current != null) current.next = symbol;
    if(root == null) root = symbol;
    return symbol;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    Node node = root;
    while(node != null) {
      if(builder.length() > 0) builder.append(' ');
      builder.append('[').append(node.getType());
      builder.append(':').append(node.getValue()).append(']');
      node = node.next;
    }
    return builder.toString();
  }

  public boolean match(String text) {
    int index = _match(text, root, 0);
//    System.out.println("=====> "+ index + " : "+ text.length());
    return index == text.length();
  }

  private int _match(String text, Node current, int start) {
    if(current == null) return start;
    
//    System.out.println(text.substring(start) + " : " + current.type);

    if(current.type == NodeType.CONSTANT) {
      int index = text.indexOf(current.value, start);
      if(index != start) return -1;
      return _match(text, current.next, start + current.value.length());
    } 

    if(current.type == NodeType.DIGIT) {
      int index = start;
      while(index < text.length()) {
        char c = text.charAt(index);
        if(!Character.isDigit(c)) break;
        index++;
      }
      if(index == start) return -1;
      return _match(text, current.next, index);
    }

    if(current.type == NodeType.LETTER) {
      int index = start;
      while(index < text.length()) {
        char c = text.charAt(index);
        if(!Character.isLetter(c)) break;
        index++;
      }
      if(index == start) return -1;
      return _match(text, current.next, index);
    }

    Node next = searchNext(current);
    if(next == null) {
      return text.length();
    }

//    System.out.println(next.type + " : "+ text.substring(start));
    if(next.type == NodeType.CONSTANT) {
//      System.out.println(text + " : "+ start +  " : " 
//          + current.value + " : " + text.indexOf(next.value, start));
      return nextConstant(text, next, start);
//      int index = text.indexOf(next.value, start);
//      if(index < 0) return -1;
//      return _match(text, next.next, index + next.value.length());
    }

    if(next.type == NodeType.DIGIT) {
      return nextDigit(text, next, start);
//      int index = start;
//      while(index < text.length()) {
//        char c = text.charAt(index);
//        if(Character.isDigit(c)) {
//          if(index == text.length()) return -1;
//          int _return = _match(text, next, index);
//          if(_return != -1) return _return;
//        }
//        index++;
//      }
//      return -1;
    }

    //next type is letter
//    int index = start;
//    while(index < text.length()) {
//      char c = text.charAt(index);
//      if(Character.isLetter(c)) break;
//      index++;
//    }
//    if(index == text.length()) return -1;
//    return _match(text, next, index);
    
    return nextText(text, next, start);
  }
  
  private int nextConstant(String text, Node next, int start) {
    int index = text.indexOf(next.value, start);
    if(index < 0) return -1;
    int _return = _match(text, next.next, index + next.value.length());
    if(_return != -1) return _return;
    return nextConstant(text, next, start + next.value.length());
  }
  
  private int nextDigit(String text, Node next, int start) {
    int index = start;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isDigit(c)) break;
      index++;
    }
    
    if(index == text.length()) return -1;
    int _return = _match(text, next, index);
    if(_return != -1) return _return;
    
    index = start;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(!Character.isDigit(c)) break;
      index++;
    }
    
    return nextDigit(text, next, index);
  }
  
  private int nextText(String text, Node next, int start) {
    int index = start;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(Character.isLetter(c)) break;
      index++;
    }
    
    if(index == text.length()) return -1;
    int _return = _match(text, next, index);
    if(_return != -1) return _return;
    
    index = start;
    while(index < text.length()) {
      char c = text.charAt(index);
      if(!Character.isLetter(c)) break;
      index++;
    }
    
    return nextText(text, next, index);
  }

  private Node searchNext(Node current) {
    Node node = current.next;
    while(node != null) {
      if(node.type == NodeType.CONSTANT
          || node.type == NodeType.DIGIT
          || node.type == NodeType.LETTER) {
        return node;
      }
      node = node.next; 
    }
    return null;
  }

  public boolean match2(String text) {
    //	  System.out.println(text);
    //	  System.out.println(root);
    if(root == null) {
      return text.length() == 0;
    }

    Node current  = root;
    Node _root = root.clone();

    int start = 0;
    //check constant


    //		System.out.println(text);
    //		current  = _root;
    //		while(current != null) {
    //			System.out.println(" da xay ra |"
    //					+ current.value 
    //					+ "| type= " + current.type 
    //					+ ", start=" + current.start 
    //					+", end="+ current.end());
    //			current = current.next;
    //		}

    current  = _root;
    while(current != null) {
      if(current.type == NodeType.CONSTANT) {
        int index = text.indexOf(current.value, start);
        if(index < 0) return false;
        current.start = index;
        current.end = index + current.value.length();
        start = index + current.value.length();
      } else { 
        current.start = start;
      }
      current = current.next; 
    }

    System.out.println(text);

    current  = _root;
    System.out.println(current.type);
    while(current != null) {
      if(current.type == NodeType.DIGIT) {
        start = current.start;
        while(start < text.length()) {
          char c = text.charAt(start);
          if(Character.isDigit(c)) {
            current.start = start;
            break;
          }
          start++;
        }

        while(start < text.length()) {
          char c = text.charAt(start);
          if(!Character.isDigit(c)) {
            current.end = start;
            break;
          }
          start++;
        }

        if(current.start < 0 || current.end  < 0) return false;
      }
      current = current.next; 
    }

    System.out.println("=== > "+ text);
    //		current  = _root;
    //		while(current != null) {
    //		  System.out.println(" da xay ra |"
    //		      + current.value 
    //		      + "| type= " + current.type 
    //		      + ", start=" + current.start 
    //		      +", end="+ current.end());
    //		  current = current.next;
    //		}
    //		System.out.println("===========================");

    current  = _root;
    while(current != null) {
      if(current.type == NodeType.LETTER) {
        start = current.start;
        while(start < text.length()) {
          char c = text.charAt(start);
          if(Character.isLetter(c)) {
            current.start = start;
            break;
          }
          start++;
        }

        while(start < text.length()) {
          char c = text.charAt(start);
          if(!Character.isLetter(c)) {
            current.end = start;
            break;
          }
          start++;
        }

        if(start >= text.length()) current.end = start;

        System.out.println("check vao day: " + text);
        System.out.println("hihihi " 
            + current.start + " : " + current.end + " : "  
            + (current.start < 0 || current.end  < 0));

        if(current.start < 0 || current.end  < 0) return false;
      }
      current = current.next; 
    }

    //		current  = _root;
    //		while(current != null) {
    //			System.out.println(" da xay ra |"
    //					+ current.value 
    //					+ "| type= " + current.type 
    //					+ ", start=" + current.start 
    //					+", end="+ current.end());
    //			current = current.next;
    //		}
    //		System.out.println("===========================");

    current = _root;
    if(current.start != 0) return false;

    while(current != null) {
      if(current.next == null) break;

      //			System.out.println(" da xay ra |"
      //					+ current.value 
      //					+ "| type= " + current.type 
      //					+ ", start=" + current.start 
      //					+ ", end="+ current.end() 
      //					+ ", next_start="  + current.next.start);

      if(current.end() == current.next.start) {
        current = next(current);
        continue;
      }

      //			System.out.println(text.substring(current.end(), current.next.start));
      return false;
    }

    //		System.out.println(" da xay ra |"
    //				+ current.value 
    //				+ "| type= " + current.type 
    //				+ ", start=" + current.start 
    //				+ ", end="+ current.end());

    if(current.end < 0 
        && current.type == NodeType.ANY) return true;

    if(current.end() == text.length()) return true;

    return false;
  }

  private Node next(Node node) {
    //		System.out.println(" keke 0 "+ node.getValue());
    if(node.next != null
        && node.next.type == NodeType.ANY
        && node.next.next != null
        && node.next.next.type == NodeType.ANY) {
      //			System.out.println("kekeke 1 "+ node.getValue());
      return next(node.next);
    }
    //		System.out.println("kekeke "+ node.getValue());
    return node.next;
  }

  public static enum NodeType {
    ANY, LETTER, DIGIT, CONSTANT;
  }

  public static class Node {

    private NodeType type;
    private String value;

    private Node next;
    private int start;
    private int end = -1;

    public Node(String value, NodeType type) {
      this.type = type;
      this.value = value;
    }

    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public boolean match(String text) {
      if(type == NodeType.ANY) return true;
      if(type == NodeType.LETTER) {
        return isLeter(text);
      } 
      if(type == NodeType.DIGIT) {
        return isDigit(text);
      }

      return value.equalsIgnoreCase(text);
    }

    private boolean isDigit(String _value) {
      int index = 0;
      while(index < _value.length()) {
        char c = _value.charAt(index);
        if(!Character.isDigit(c)) return false;
        index++;
      }
      return true;
    }

    private boolean isLeter(String _value) {
      int index = 0;
      while(index < _value.length()) {
        char c = _value.charAt(index);
        if(!Character.isLetter(c)) return false;
        index++;
      }
      return true;
    }

    public int end() {
      if(end  > -1) return end;
      if(type != NodeType.ANY) return -1;
      Node symbol = next;
      while(symbol != null) {
        if(symbol.type != NodeType.ANY) {
          end = symbol.start;
          break;
        }
        symbol = symbol.next;
      }
      return end;
    }


    public Node clone() {
      Node symbol = new Node(value, type);
      if(next != null) symbol.next = next.clone();
      return symbol;
    }
  }

  /*private boolean valid(String value, char symbol) {
	if(symbol == '*') return true;
	if(symbol == '@') {
		int index = 0;
		while(index < value.length()) {
			char c = value.charAt(index);
			if(Character.isDigit(c)) return false;
			index++;
		}
	}

	if(symbol == '$') {
		int index = 0;
		while(index < value.length()) {
			char c = value.charAt(index);
			if(Character.isLetter(c)) return false;
			index++;
		}
	}

	return value.length() == 1 && value.charAt(0) == symbol;
}
   */
}
