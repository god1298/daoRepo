import java.io.Serializable;
import java.lang.String;
import java.util.Date;

/**
 * @date 2014-04-07 22:24:14  entity for table Student
 */
public class Student implements Serializable{

	private static final long serialVersionUID = 1L;
	private int Id;// 
	private String Name;
	private int age;
	private int gender;
	private Date createDate;

	public int getId(){
		return this.Id;
	}

	public void setId(int Id){
		 this.Id=Id;
	}
	public String getName(){
		return this.Name;
	}

	public void setName(String Name){
		 this.Name=Name;
	}
	public int getAge(){
		return this.age;
	}

	public void setAge(int age){
		 this.age=age;
	}
	public int getGender(){
		return this.gender;
	}

	public void setGender(int gender){
		 this.gender=gender;
	}
	public Date getCreateDate(){
		return this.createDate;
	}

	public void setCreateDate(Date createDate){
		 this.createDate=createDate;
	}
}