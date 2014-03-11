package jxl.common;

public class LoggerImpl extends Logger {
	
	@Override
	protected Logger getLoggerImpl(Class cl) {
		return new LoggerImpl();
	}

	@Override
	public void debug(Object message) {
		System.out.println(message);
		
	}

	@Override
	public void debug(Object message, Throwable t) {
		System.out.println(message);
		t.printStackTrace();
		
	}

	@Override
	public void error(Object message) {
		System.out.println(message);
		
	}

	@Override
	public void error(Object message, Throwable t) {
		System.out.println(message);
		t.printStackTrace();
		
	}

	@Override
	public void fatal(Object message) {
		System.out.println(message);
		
	}

	@Override
	public void fatal(Object message, Throwable t) {
		System.out.println(message);
		t.printStackTrace();
	}

	@Override
	public void info(Object message) {
		System.out.println(message);
		
	}

	@Override
	public void info(Object message, Throwable t) {
		System.out.println(message);
		t.printStackTrace();
		
	}

	@Override
	public void warn(Object message) {
		System.out.println(message);
		
	}

	@Override
	public void warn(Object message, Throwable t) {
		System.out.println(message);
		t.printStackTrace();
		
	}

	
	
	

}
