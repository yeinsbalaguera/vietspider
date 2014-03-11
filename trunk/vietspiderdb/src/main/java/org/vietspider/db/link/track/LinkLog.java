package org.vietspider.db.link.track;

import java.io.Serializable;
import java.util.Date;

import org.vietspider.common.text.CalendarUtils;

public class LinkLog implements Serializable {

  private static final long serialVersionUID = -345342293623L;

  //	public final static int STATUS_FOUND = 0;
  //	public final static int STATUS_VISIT = 1;
  //	public final static int STATUS_EXTRACT = 2;

  public final static int TYPE_INVALID = 0;
  public final static int TYPE_HOME = 1;
  public final static int TYPE_SCRIPT = 2;
  public final static int TYPE_LINK = 3;
  public final static int TYPE_DATA = 4;

  public final static int PHASE_DOWNLOAD = 0;
  public final static int PHASE_EXTRACT = 1;
  public final static int PHASE_SOURCE = 2;
  public final static int PHASE_PLUGIN = 3;
  public final static int PHASE_RSS = 4;
  //	public final static int ERROR_RUNTIME = 3;
  //	public final static int PHASE._NO_ERROR = 4;
  //	public final static int ERROR_TIMEOUT = 2;

  private String url;
  private String channel;

  private long bytes;
  private String dataId;

  private String message;

  private int level;
  //	private int status = STATUS_FOUND;
  private int type = TYPE_INVALID;
  private int phase = PHASE_DOWNLOAD	;

  private Integer id;
  private long time;
  private int urlCode;
  
  private Throwable throwable;

  public LinkLog() {
  }

  public LinkLog(String channel, String url) {
    this.channel = channel;
    this.url = url;
    this.urlCode = url.hashCode();
    this.time = System.currentTimeMillis();
    //generate id;
  }

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }

  public int getLevel() { return level; }
  public void setLevel(int level) { this.level = level; }

  public long getTime() { return time; }
  public void setTime(long time) { this.time = time; }

  //	public int getStatus() { return status; }
  //	public void setStatus(int status) { this.status = status; }

  public int getType() { return type; }
  public void setType(int type) { this.type = type; }

  public int getPhase() { return phase; }
  public void setPhase(int error) { this.phase = error; }

  public long getBytes() { return bytes; }
  public void setBytes(long bytes) { this.bytes = bytes; }

  public String getChannel() { return channel; }
  public void setChannel(String channel) { this.channel = channel; }

  public Integer getId() {
    if(id != null) return id;
    StringBuilder builder = new StringBuilder();
    builder.append(url).append(' ').append(level).append(' ').append(time);
    id = new Integer(builder.toString().hashCode());
    return id; 
  }
  public void setId(Integer id) { this.id = id; }

  public int getUrlCode() { return urlCode; }
  public void setUrlCode(int urlCode) { this.urlCode = urlCode; }

  public String getDataId() { return dataId; }
  public void setDataId(String dataId) { this.dataId = dataId; }

  public String getMessage() {return message; }
  public void setMessage(String message) { this.message = message; }
  
  public Throwable getThrowable() { return throwable; }
  public void setThrowable(Throwable exception) { this.throwable = exception; }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(CalendarUtils.getDateTimeFormat().format(new Date(time)));
    builder.append(" | ");

    builder.append(channel).append(" | ");
    builder.append(url).append(" | ");

    builder.append("ID: ").append(getId()).append(" | ");
    builder.append("URL Code: ").append(urlCode).append(" | ");
    builder.append("Level: ").append(level).append(" | ");
    builder.append("Phase: ");
    if(phase == PHASE_DOWNLOAD) {
      builder.append("Download").append(" | ");
    } else if(phase == PHASE_EXTRACT) {
      builder.append("Extract").append(" | ");
    } else if(phase == PHASE_SOURCE) {
      builder.append("Source").append(" | ");
    } else if(phase == PHASE_PLUGIN) {
      builder.append("Plugin").append(" | ");
    } else if(phase == PHASE_RSS) {
      builder.append("RSS").append(" | ");
    }
    builder.append("Type: ");
    if(type == TYPE_INVALID) {
      builder.append("Invalid").append(" | ");
    } else if(type == TYPE_HOME) {
      builder.append("Homepage").append(" | ");
    } else if(type == TYPE_SCRIPT) {
      builder.append("Function").append(" | ");
    } else if(type == TYPE_LINK) {
      builder.append("Link").append(" | ");
    } else if(type == TYPE_DATA) {
      builder.append("Data").append(" | ");
    }
    builder.append(message).append(" | ");
    builder.append(bytes).append(" | ");
    if(dataId != null) {
      builder.append(dataId);
    } else {
      builder.append("N/A");
    }

    return builder.toString();
  }
  
  public String toShortString() {
    StringBuilder builder = new StringBuilder();

    builder.append(url).append("\r\n");
    builder.append(message).append("\r\n");
    builder.append(CalendarUtils.getDateTimeFormat().format(new Date(time)));
    builder.append(" | ");

    builder.append("ID: ").append(getId()).append(" | ");
    builder.append("URL Code: ").append(urlCode).append(" | ");
    builder.append("Level: ").append(level).append(" | ");
    builder.append("Phase: ");
    if(phase == PHASE_DOWNLOAD) {
      builder.append("Download").append(" | ");
    } else if(phase == PHASE_EXTRACT) {
      builder.append("Extract").append(" | ");
    } else if(phase == PHASE_SOURCE) {
      builder.append("Source").append(" | ");
    } else if(phase == PHASE_PLUGIN) {
      builder.append("Plugin").append(" | ");
    } else if(phase == PHASE_RSS) {
      builder.append("RSS").append(" | ");
    }
    builder.append("Type: ");
    if(type == TYPE_INVALID) {
      builder.append("Invalid").append(" | ");
    } else if(type == TYPE_HOME) {
      builder.append("Homepage").append(" | ");
    } else if(type == TYPE_SCRIPT) {
      builder.append("Function").append(" | ");
    } else if(type == TYPE_LINK) {
      builder.append("Link").append(" | ");
    } else if(type == TYPE_DATA) {
      builder.append("Data").append(" | ");
    }
    builder.append("Size: ").append(bytes).append(" | ");
    if(dataId != null) {
      builder.append(dataId);
    } else {
      builder.append("N/A");
    }
    builder.append("\r\n");
    
    if(throwable != null) {
      builder.append("EXCEPTION: ").append(throwable.toString()).append("\r\n");
      StackTraceElement[] traces = throwable.getStackTrace();
      for(StackTraceElement ele : traces) {
        int lineNumber = ele.getLineNumber();
        if(lineNumber > -1) {
          builder.append(ele.toString() + ": " +  String.valueOf(lineNumber)).append("\r\n");
        } else {
          builder.append(ele.toString()).append("\r\n");
        }
      }
      builder.append("\r\n");
    }
    
    builder.append("\r\n");

    return builder.toString();
  }

}

