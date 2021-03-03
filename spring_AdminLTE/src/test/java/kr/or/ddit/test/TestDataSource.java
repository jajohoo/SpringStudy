package kr.or.ddit.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:kr/or/ddit/context/root-context.xml")
public class TestDataSource {
	
	@Autowired
	private DataSource dataSource;
	
	private Connection conn;
	
	@Before
	public void init()throws SQLException{
		conn=dataSource.getConnection();
	}
	
	@Test
	public void testConnection() {
		Assert.assertNotNull(conn);
	}
	
	@Test
	public void testSQL() throws SQLException {
		Assert.assertNotNull(conn);
		
		Statement stmt = conn.createStatement();
		
		String sql = "select * from member";
		
		ResultSet rs = stmt.executeQuery(sql);
		
		List<MemberVO> memberList = new ArrayList<MemberVO>();
		
		while(rs.next()) {
			
			MemberVO member = new MemberVO();
			member.setId(rs.getString("id"));
			member.setPwd(rs.getString("pwd"));
			
			memberList.add(member);
		}
		
		rs.close();
		stmt.close();
		Assert.assertEquals(7, memberList.size());
		
	}
	
	@After
	public void after()throws SQLException{
		if(conn!=null)conn.close();
	}
	
}


class MemberVO{
	//mockup형태로 만든다
	private String id;
	private String pwd;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	@Override
	public String toString() {
		return "MemberVO [id=" + id + ", pwd=" + pwd + "]";
	}
	
	
	
}
