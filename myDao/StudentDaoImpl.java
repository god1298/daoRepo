
/**
 * @date 2014-04-10 14:12:27  dao for table Student
 */
public class StudentDaoImpl implements StudentDao{

	public int getStudentCount(Map<String, Object> condition)throws Exception{
		int rowCount = 0;
		StringBuilder sql = new StringBuilder(100);
		sql.append("select count(1) from Student where 1=1 ");
		if(condition.get("id") != null){
			sql.append(" and id=").append(condition.get(id).toString());
		}
		if(condition.get("name") != null){
			sql.append(" and name=").append(condition.get(name).toString());
		}
		if(condition.get("age") != null){
			sql.append(" and age=").append(condition.get(age).toString());
		}
		if(condition.get("score") != null){
			sql.append(" and score=").append(condition.get(score).toString());
		}
		if(condition.get("departId") != null){
			sql.append(" and departId=").append(condition.get(departId).toString());
		}
		rowCount = jdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), Integer.class);
		return rowCount;
	}

	public List<Map<String, Object>> findStudentbyCondition(Map<String, Object> condition)throws Exception{
		StringBuilder sql = new StringBuilder(100);
		sql.append("select * from Student where 1=1 ");
		if(condition.get("id") != null){
			sql.append(" and id=").append(condition.get(id).toString());
		}
		if(condition.get("name") != null){
			sql.append(" and name=").append(condition.get(name).toString());
		}
		if(condition.get("age") != null){
			sql.append(" and age=").append(condition.get(age).toString());
		}
		if(condition.get("score") != null){
			sql.append(" and score=").append(condition.get(score).toString());
		}
		if(condition.get("departId") != null){
			sql.append(" and departId=").append(condition.get(departId).toString());
		}
		if(condition.get("limit") != null){
			sql.append(" limit ").append(condition.get("rowOffset").append(",").append(condition.get("pageSize"));
		}
		return jdbcTemplate.getJdbcOperations().queryForList(sql.toString());
	}

	public Student findStudentbyId(Student student)throws Exception{
		StringBuilder sql = new StringBuilder(100);
		sql.append("select * from Student where 1=1  and id=:id and name=:name");
		Map namedParameters = new HashMap();
		namedParameters.put("id",id);
		namedParameters.put("name",name);
		return jdbcTemplate.queryForObject(sql.toString(), namedParameters, new BeanPropertyRowMapper<Student>(Student.class));
	}

	public Student findStudentbyId(int id)throws Exception{
		StringBuilder sql = new StringBuilder(100);
		sql.append("select * from Student where 1=1 ");
		Map namedParameters = new HashMap();
		namedParameters.put("###","###");
		return jdbcTemplate.queryForObject(sql.toString(), namedParameters, new BeanPropertyRowMapper<Student>(Student.class));
	}

	public int insertStudent(Student student)throws Exception{
		int rowCount = 0;
		StringBuilder sql = new StringBuilder(100);
		sql.append("insert into Student");
		sql.append("(id,name,age,score,departId)");
		sql.append(" values");
		sql.append("(:id,:name,:age,:score,:departId)");
		rowCount = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(student));
		return rowCount;
	}

	public int updateStudent(Student student)throws Exception{
		int rowCount = 0;
		StringBuilder sql = new StringBuilder(100);
		sql.append("update Student set ");
		sql.append("id=:id,name=:name,age=:age,score=:score,departId=:departId");
		sql.append(" where 1=1 ");
		sql.append(" and id=:id and name=:name");
		rowCount = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(student));
		return rowCount;
	}

	public int deleteStudent(Student student)throws Exception{
		int rowCount = 0;
		StringBuilder sql = new StringBuilder(100);
		sql.append("delete from Student where 1=1 ");
		sql.append(" and id=:id and name=:name");
		rowCount = jdbcTemplate.update(sql.toString(), new BeanPropertySqlParameterSource(student));
		return rowCount;
	}

}