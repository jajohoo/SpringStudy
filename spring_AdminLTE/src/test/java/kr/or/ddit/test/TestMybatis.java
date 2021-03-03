package kr.or.ddit.test;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:kr/or/ddit/context/root-context.xml")
public class TestMybatis {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private SqlSession session;
	
	@Test
	public void testSqlSessionFactory() {
		Assert.assertNotNull(sqlSessionFactory);
	}
	
	@Test // mapper문제를 찾기
	public void testsqlSession()throws SQLException {
		Collection<String> mapNames = 
				(Collection<String>)session.getConfiguration().getMappedStatementNames();
		
		Assert.assertTrue(mapNames.contains("Member-Mapper.selectSearchMemberList"));
	}
	
}
