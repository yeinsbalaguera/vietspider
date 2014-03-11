/*
 * InternetAddress.java
 * Copyright (C) 2002, 2004 The Free Software Foundation
 * 
 * This file is part of GNU JavaMail, a library.
 * 
 * GNU JavaMail is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * GNU JavaMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As a special exception, if you link this library with other files to
 * produce an executable, this library does not by itself cause the
 * resulting executable to be covered by the GNU General Public License.
 * This exception does not however invalidate any other reasons why the
 * executable file might be covered by the GNU General Public License.
 */
package org.vietspider.nlp.impl;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * An RFC 822 address.
 *
 * @author <a href="mailto:dog@gnu.org">Chris Burdess</a>
 * @version 1.4
 */
public class EmailAddress implements Cloneable {

  /*
   * The type of address.
   */
  private static final String RFC822 = "rfc822";

  /**
   * The string representation of the address.
   */
  protected String address;

  /**
   * The personal name.
   */
  protected String personal;

  /**
   * The RFC 2047 encoded version of the personal name.
   */
  protected String encodedPersonal;

  /**
   * Constructor for an empty address.
   */
  public EmailAddress()
  {
  }

  /**
   * Constructor with an RFC 822 string representation of the address.
   * Note that this parses the address in non-strict mode: this is for
   * compatibility with implementations and not with the JavaMail
   * specification.
   * @param address the address in RFC 822 format
   * @exception Exception if the parse failed
   */
  public EmailAddress(String address)
  throws Exception
  {
    this(address, false);
  }

  /**
   * Constructor with an RFC 822 string representation of the address.
   * @param address the address in RFC 822 format
   * @param strict enforce RFC 822 syntax
   * @exception Exception if the parse failed
   * @since JavaMail 1.3
   */
  public EmailAddress(String address, boolean strict)
  throws Exception
  {
    EmailAddress[] addresses = parseHeader(address, strict);
    if (addresses.length != 1)
    {
      throw new Exception("Illegal address");
    }
    this.address = addresses[0].address;
    this.personal = addresses[0].personal;
    this.encodedPersonal = addresses[0].encodedPersonal;
    if (strict)
    {
      validate(address, true, true);
    }
  }


  /**
   * Returns a copy of this address.
   */
  public Object clone()
  {
    EmailAddress clone = new EmailAddress();
    clone.address = this.address;
    clone.personal = personal;
    clone.encodedPersonal = encodedPersonal;
    return clone;
  }

  /**
   * Returns the type of this address.
   * The type of an <code>InternetAddress</code> is "rfc822".
   */
  public String getType()
  {
    return RFC822;
  }

  public boolean isGroup()
  {
    int start = address.indexOf(':');
    if (start == -1)
    {
      return false;
    }
    int end = address.length() - 1;
    return (address.charAt(end) == ';');
  }

  /**
   * Returns the members of a group address. A group may have any number of
   * members. If this address is not a group, this method returns
   * <code>null</code>.
   * @exception Exception if a parse error occurs
   * @since JavaMail 1.3
   */
  public EmailAddress[] getGroup(boolean strict)
  throws Exception
  {
    int start = address.indexOf(':');
    int end = address.length() - 1;
    if (start == -1 || address.charAt(end) == ';')
    {
      return null;
    }
    return parseHeader(address.substring(start + 1, end), strict);
  }

  /**
   * Sets the email address.
   */
  public void setAddress(String address)
  {
    this.address = address;
  }



  /**
   * Returns the email address.
   */
  public String getAddress()
  {
    return address;
  }


  /**
   * Validate this address according to the syntax rules of RFC 822.
   * This implementation checks many but not all of the syntax rules.
   * @exception Exception if the address is invalid
   * @since JavaMail 1.3
   */
  public void validate()
  throws Exception
  {
    validate(address, true, true);
  }


  public boolean equals(Object other)
  {
    if (other instanceof EmailAddress)
    {
      String otherAddress = ((EmailAddress) other).getAddress();
      return (this == other || 
          (address != null && address.equalsIgnoreCase(otherAddress)));
    }
    return false;
  }

  public int hashCode()
  {
    return (address == null) ? 0 : address.hashCode();
  }


  public static EmailAddress[] parse(String addresslist)
  throws Exception
  {
    return parse(addresslist, true);
  }

  /**
   * Parses the given comma-separated sequence of RFC 822 addresses into
   * InternetAddresses.
   * If <code>strict</code> is false, simple email addresses separated by 
   * spaces are also allowed. If <code>strict</code> is true, many (but not
   * all) of the RFC 822 syntax rules are enforced.
   * Even if <code>strict</code> is true, addresses composed of simple
   * names (with no "@domain" part) are allowed.
   * @param addresslist the comma-separated addresses
   * @param strict whether to enforce RFC 822 syntax
   * @exception Exception if the parse failed
   */
  public static EmailAddress[] parse(String addresslist, boolean strict)
  throws Exception
  {
    return parse(addresslist, strict ? STRICT : NONE);
  }

  /**
   * Parses the given comma-separated sequence of RFC 822 addresses into
   * InternetAddresses.
   * If <code>strict</code> is false, simple email addresses separated by 
   * spaces are also allowed. If <code>strict</code> is true, many (but not
   * all) of the RFC 822 syntax rules are enforced.
   * @param addresslist the comma-separated addresses
   * @param strict whether to enforce RFC 822 syntax
   * @exception Exception if the parse failed
   * @since JavaMail 1.3
   */
  public static EmailAddress[] parseHeader(String addresslist,
      boolean strict)
  throws Exception
  {
    return parse(addresslist, strict ? STRICT_OR_LAX : LAX);
  }

  private static final int NONE = 0x00;
  private static final int LAX = 0x01;
  private static final int STRICT = 0x02;
  private static final int STRICT_OR_LAX = 0x03;

  private static EmailAddress[] parse(String addresslist, int rules)
  throws Exception
  {
    /*
     * address := mailbox / group ; one addressee, named list
     * group := phrase ":" [#mailbox] ";"
     * mailbox := addr-spec / phrase route-addr ; simple address,
     *                                          ; name & addr-spec
     * route-addr := "<" [route] addr-spec ">"
     * route := 1#("@" domain) ":" ; path-relative
     * addr-spec := local-part "@" domain ; global address
     * local-part := word *("." word) ; uninterpreted, case-preserved
     * domain := sub-domain *("." sub-domain)
     * sub-domain := domain-ref / domain-literal
     * domain-ref := atom ; symbolic reference
     */

    // NB I have been working on this parse for about 8 hours now.
    // It is very likely I am starting to lose the plot.
    // If anyone wants to work on it, I strongly recommend you write some
    // kind of tokenizer and attack it from that direction.

    boolean inGroup = false;
    boolean gotDelimiter = false;
    boolean inAddress = false;
    int len = addresslist.length();
    int pEnd = -1;
    int pStart = -1;
    int start = -1;
    int end = -1;
    ArrayList<EmailAddress> acc = new ArrayList<EmailAddress>();

    int pos;
    for (pos = 0; pos < len; pos++)
    {
      char c = addresslist.charAt(pos);
      switch (c)
      {
      case '\t':
      case '\n':
      case '\r':
      case ' ':
        break;

      case '<': // bra-ket delimited address
        inAddress = true;
        if (gotDelimiter)
        {
          throw new Exception("Too many route-addr");
        }
        if (!inGroup)
        {
          start = pStart;
          if (start >= 0)
          {
            end = pos;
          }
          pStart = pos + 1;
        }
        pos++;
        boolean inQuote = false;
        boolean gotKet = false;
        while (pos<len && !gotKet)
        {
          char c2 = addresslist.charAt(pos);
          switch (c2)
          {
          case '"':
            inQuote = !inQuote;
            break;
          case '>':
            if (!inQuote)
            {
              gotKet = true;
              pos--;
            }
            break;
          case '\\':
            pos++;
            break;
          }
          pos++;
        }
        if (!gotKet && pos >= len)
        {
          if (inQuote)
          {
            throw new Exception("Unmatched '\"'");
          }
          throw new Exception("Unmatched '<'");
        }
        gotDelimiter = true;
        pEnd = pos;
        break;
      case '>':
        throw new Exception("Unmatched '>'");

      case '(': // paren delimited personal
        inAddress = true;
        if (pStart >= 0 && pEnd == -1)
        {
          pEnd = pos;
        }
        if (start == -1)
        {
          start = pos + 1;
        }
        pos++;
        int parenCount = 1;
        while (pos < len && parenCount > 0)
        {
          c = addresslist.charAt(pos);
          switch (c)
          {
          case '(':
            parenCount++;
          break;
          case ')':
            parenCount--;
            break;
          case '\\':
            pos++;
            break;
          }
          pos++;
        }
        if (parenCount > 0)
        {
          throw new Exception("Unmatched '('");
        }
        pos--;
        if (end == -1)
        {
          end = pos;
        }
        break;
      case ')':
        throw new Exception("Unmatched ')'");

      case '"': // quote delimited personal
        inAddress = true;
        if (pStart == -1)
        {
          pStart = pos;
        }
        pos++;
        boolean gotQuote = false;
        while (pos < len && !gotQuote)
        {
          c = addresslist.charAt(pos);
          switch (c)
          {
          case '"':
            gotQuote = true;
            pos--;
            break;
          case '\\':
            pos++;
            break;
          }
          pos++;
        }
        if (pos >= len)
        {
          throw new Exception("Unmatched '\"'");
        }
        break;

      case '[':
        inAddress = true;
        pos++;
        boolean gotBracket = false;
        while (pos < len && !gotBracket)
        {
          c = addresslist.charAt(pos);
          switch (c)
          {
          case ']':
            gotBracket = true;
            pos--;
            break;
          case '\\':
            pos++;
            break;
          }
          pos++;
        }
        if (pos >= len)
        {
          throw new Exception("Unmatched '['");
        }
        break;

      case ',': // address delimiter
        if (pStart == -1)
        {
          gotDelimiter = false;
          inAddress = false;
          pEnd = -1;
          break;
        }
        if (inGroup)
        {
          break;
        }
        if (pEnd == -1)
        {
          pEnd = pos;
        }
        {
          String addressText = addresslist.substring(pStart, pEnd);
          addressText = addressText.trim();
          if (inAddress ||(rules | STRICT_OR_LAX) != 0)
          {
            if ((rules & STRICT) != 0 ||(rules & LAX) == 0)
            {
              validate(addressText, gotDelimiter, false);
            }
            EmailAddress address = new EmailAddress();
            address.setAddress(addressText);
            if (start >= 0)
            {
              String personal = addresslist.substring(start, end);
              personal = personal.trim();
              address.encodedPersonal = unquote(personal);
              start = end = -1;
            }
            acc.add(address);
          }
          else
          {
            StringTokenizer st = new StringTokenizer(addressText);
            while (st.hasMoreTokens())
            {
              addressText = st.nextToken();
              validate(addressText, false, false);
              EmailAddress address = new EmailAddress();
              address.setAddress(addressText);
              acc.add(address);
            }
          }
        }
        gotDelimiter = false;
        inAddress = false;
        pStart = -1;
        pEnd = -1;
        break;

      case ':': // group indicator
        inAddress = true;
        if (inGroup)
        {
          throw new Exception("Cannot have nested group");
        }
        inGroup = true;
        break;
      case ';': // group delimiter
        if (!inGroup)
        {
          throw new Exception("Unexpected ';'");
        }
        inGroup = false;
        pEnd = pos + 1;
        {
          String addressText = addresslist.substring(pStart, pEnd);
          addressText = addressText.trim();
          EmailAddress address = new EmailAddress();
          address.setAddress(addressText);
          acc.add(address);
        }
        gotDelimiter = false;
        pStart = pEnd = -1;
        break;

      default:
        if (pStart == -1)
        {
          pStart = pos;
        }
        break;
      }
    }

    if (pStart > -1)
    {
      if (pEnd == -1)
      {
        pEnd = pos;
      }
      String addressText = addresslist.substring(pStart, pEnd);
      addressText = addressText.trim();
      if (inAddress ||(rules | STRICT_OR_LAX) != 0)
      {
        if ((rules & STRICT) != 0 ||(rules & LAX) == 0)
        {
          validate(addressText, gotDelimiter, false);
        }
        EmailAddress address = new EmailAddress();
        address.setAddress(addressText);
        if (start >= 0)
        {
          String personal = addresslist.substring(start, end);
          personal = personal.trim();
          address.encodedPersonal = unquote(personal);
        }
        acc.add(address);
      }
      else
      {
        StringTokenizer st = new StringTokenizer(addressText);
        while (st.hasMoreTokens())
        {
          addressText = st.nextToken();
          validate(addressText, false, false);
          EmailAddress address = new EmailAddress();
          address.setAddress(addressText);
          acc.add(address);
        }
      }
    }

    EmailAddress[] addresses = new EmailAddress[acc.size()];
    acc.toArray(addresses);
    return addresses;
  }

  private static void validate(String address, boolean gotDelimiter,
      boolean strict)
  throws Exception
  {
    // TODO What happens about addresses with quoted strings?
    int pos = 0;
    if (!strict || gotDelimiter)
    {
      int i = address.indexOf(',', pos);
      if (i < 0)
      {
        i = address.indexOf(':', pos);
      }
      while (i > -1)
      {
        if (address.charAt(pos) != '@')
        {
          throw new Exception("Illegal route-addr");
        }
        if (address.charAt(i) != ':')
        {
          i = address.indexOf(',', pos);
          if (i < 0)
          {
            i = address.indexOf(':', pos);
          }
        }
        else
        {
          pos = i + 1;
          i = -1;
        }
      }
    }

    // Get atomic parts
    String localName = address;
    String domain = null;
    int atIndex = address.indexOf('@', pos);
    if (atIndex > -1)
    {
      if (atIndex == pos)
      {
        throw new Exception("Missing local name");
      }
      if (atIndex == address.length() - 1)
      {
        throw new Exception("Missing domain");
      }
      localName = address.substring(pos, atIndex);
      domain = address.substring(atIndex + 1);
    }
    else if (strict)
    {
      throw new Exception("Missing final @domain");
    }

    // Check atomic parts
    String illegalWS = "\t\n\r ";
    int len = 4; // illegalWS.length()
    for (int i = 0; i < len; i++)
    {
      if (address.indexOf(illegalWS.charAt(i)) > -1)
      {
        throw new Exception("Illegal whitespace");
      }
    }
    String illegalName = "\"(),:;<>@[\\]";
    len = 12; // illegalName.length()
    for (int i = 0; i < len; i++)
    {
      if (localName.indexOf(illegalName.charAt(i)) > -1)
      {
        throw new Exception("Illegal local name");
      }
    }
    if (domain != null)
    {
      for (int i = 0; i < len; i++)
      {
        if (domain.indexOf(illegalName.charAt(i)) > -1)
        {
          throw new Exception("Illegal domain");
        }
      }
    }
  }
  /*
   * Un-quote-escapes the specified text.
   */
  private static String unquote(String text)
  {
    int len = text.length();
    if (len > 2 && text.charAt(0) == '"' && text.charAt(len - 1) == '"')
    {
      text = text.substring(1, len - 1);
      if (text.indexOf('\\') > -1)
      {
        len -= 2;
        StringBuffer buffer = new StringBuffer(len);
        for (int i = 0; i < len; i++)
        {
          char c = text.charAt(i);
          if (c == '\\' && i <(len - 1))
          {
            c = text.charAt(++i);
          }
          buffer.append(c);
        }
        text = buffer.toString();
      }
    }
    return text;
  }

}
