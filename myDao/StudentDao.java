
/**
 * @date 2014-04-10 14:12:27  dao for table Student
 */
public interface StudentDao{

	public int getStudentCount(Map<String, Object> condition)throws Exception;
	public List<Map<String, Object>> findStudentbyCondition(Map<String, Object> condition)throws Exception;
	public Student findStudentbyId(Student student)throws Exception;
	public Student findStudentbyId(int id)throws Exception;
	public int insertStudent(Student student)throws Exception;
	public int updateStudent(Student student)throws Exception;
	public int deleteStudent(Student student)throws Exception;
}