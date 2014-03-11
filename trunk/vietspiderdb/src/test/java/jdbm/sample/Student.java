package jdbm.sample;

import java.io.Serializable;

public class Student implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private Long storageId;
	private String id;
	private String name;
	private String email;
	private String classId;
	
	public Student() {
	}
	
	public Student(String classId, String id, String name, String email) {
		this.classId = classId;
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	public String getClassId() { return classId; }
	public void setClassId(String classId) { this.classId = classId; }

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public Long getStorageId() { return storageId; }
	public void setStorageId(Long longId) { this.storageId = longId; }
	
	
}
