import java.io.Serializable;
import java.lang.String;

/**
 * @date 2014-04-10 14:12:27  entity for table Student
 */
public class Student implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int age;
	private int score;
	private int departId;

	public int getId(){
		return this.id;
	}

	public void setId(int id){
		 this.id=id;
	}
	public String getName(){
		return this.name;
	}

	public void setName(String name){
		 this.name=name;
	}
	public int getAge(){
		return this.age;
	}

	public void setAge(int age){
		 this.age=age;
	}
	public int getScore(){
		return this.score;
	}

	public void setScore(int score){
		 this.score=score;
	}
	public int getDepartId(){
		return this.departId;
	}

	public void setDepartId(int departId){
		 this.departId=departId;
	}
}