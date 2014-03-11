package jdbm.sample;

import java.io.File;
import java.util.List;

public class SimpleTest {
	
	private static void saveData(StudentStorage storage) throws Exception {
		String classId = "C0801M";

		Student student = 
			new Student(classId, "S6454", "Nguyen Van A", "vana@yahoo.com");
		storage.write(student);

		student = 
			new Student(classId, "S3453", "Nguyen Van B", "vanb@yahoo.com");
		storage.write(student);

		classId = "C0810I";

		student = 
			new Student(classId, "S2344", "Nguyen Thi B", "thib23@gmail.com");
		storage.write(student);

		student = 
			new Student(classId, "S2341", "Tran Van C", "c44523@yahoo.com");
		storage.write(student);
	}

	public static void main(String[] args) throws Exception {
		File folder = new File("D:\\Temp\\jdbm\\");
		File [] sfiles = folder.listFiles();
		boolean init = sfiles == null || sfiles.length < 1;

		StudentStorage storage = new StudentStorage(new File(folder, "data"));
		try {
//			System.out.println(sfiles.length);
			if(init) saveData(storage);
			
			String clazzId = "C0801M";
			
			System.out.println(" query data by class id \"" + clazzId + "\n");
			List<Student> students = storage.getByClass("C0801M");
			for(Student ele : students) {
				System.out.println(ele.getId() + " : " + ele.getClassId() + " : " + ele.getName());
			}
			
			System.out.println();
			
			System.out.println("update data");
			Student student = storage.getById("S6454");
			student.setName("Nhu Dinh Thuan");
			storage.write(student);
			
			System.out.println();
			
			System.out.println(" query data by class id \"" + clazzId + "\n");
			students = storage.getByClass(clazzId);
			for(Student ele : students) {
				System.out.println(ele.getId() + " : " + ele.getClassId() + " : " + ele.getName());
			}
			
			System.out.println();
			
			System.out.println("remove data "+ student);
			storage.remove(student);
			
			System.out.println();
			
			System.out.println(" query data by class id \"" + clazzId + "\n");
			students = storage.getByClass(clazzId);
			for(Student ele : students) {
				System.out.println(ele.getId() + " : " + ele.getClassId() + " : " + ele.getName());
			}
			
			
		} finally {
			storage.close();
		}
	}

}
