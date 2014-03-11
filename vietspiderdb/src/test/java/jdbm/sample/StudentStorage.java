package jdbm.sample;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;

//http://code.google.com/p/jdbm2/

public class StudentStorage {

	private final static int MAX_RECORD = 1000;
	private RecordManager recman;
	private PrimaryStoreMap<Long, Student> main;

	private SecondaryTreeMap<String, Long, Student> idIndex;
	private SecondaryTreeMap<String, Long, Student> classIndex;
	private SecondaryTreeMap<String, Long, Student> nameIndex;

	private int counter;

	public StudentStorage(File file) throws Exception {
		recman = RecordManagerFactory.createRecordManager(file.getAbsolutePath());
		//class jdbm.helper.PrimaryStoreMapImpl
		main = recman.storeMap("students");
		idIndex = main.secondaryTreeMap("idIndex",
				new SecondaryKeyExtractor<String, Long, Student>() {
			public String extractSecondaryKey(Long key, Student value) {
				return value.getId();
			}					
		});

		nameIndex = main.secondaryTreeMap("nameIndex",
				new SecondaryKeyExtractor<String, Long, Student>() {
			public String extractSecondaryKey(Long key, Student value) {
				return value.getName();
			}					
		});
		
		classIndex = main.secondaryTreeMap("classIndex",
				new SecondaryKeyExtractor<String, Long, Student>() {
			public String extractSecondaryKey(Long key, Student value) {
				return value.getClassId();
			}					
		});
	}

	public long size() { return main.size(); }

	public void write(Student student) throws Exception {
		if(student.getStorageId() != null) {
			main.put(student.getStorageId(), student);
			commit();
			return;
		}
		main.putValue(student);
		commit();
	}

	public Student getById(String id) {
		Iterable<Long> iterable = idIndex.get(id);
		if(iterable == null) return null;
		Iterator<Long> keyIterator = iterable.iterator();
		if(!keyIterator.hasNext()) return null;
		Long key = keyIterator.next();
		Student student = main.get(key) ;
		student.setStorageId(key);
		return student;
	}
	
	public List<Student> getByClass(String clazz) {
//		Iterator<String> iterator = classIndex.keySet().iterator();
//		while(iterator.hasNext()) {
//			System.out.println(iterator.next());
//		}
		
		return this.<String>get(clazz, classIndex);
	}

	public List<Student> getByName(String name) {
		return this.<String>get(name, nameIndex);
	}
	
	private <T> List<Student> get(String fieldValue, 
			SecondaryTreeMap<T, Long, Student> index) {
		List<Student> list = new ArrayList<Student>();
		
		Iterable<Long> iterable = index.get(fieldValue);
		if(iterable == null) return list;

		Iterator<Long> keyIterator = iterable.iterator();
		while(keyIterator.hasNext()) {
			Long key = keyIterator.next();
			Student student = main.get(key);
			student.setStorageId(key);
			list.add(student);
		}

		return list;
	}

	public void remove(Student student) throws Exception {
		if(student.getStorageId() == null) {
			throw new Exception ("Storage id not found!");
		}
		main.remove(student.getStorageId());
		commit();
	}

	private void commit() throws Exception {
		counter++;
		if(counter < 10000) return;
		counter = 0; 
		recman.commit();
	}

	public void close()  throws Exception {
		recman.commit();
		recman.close();
	}

}
