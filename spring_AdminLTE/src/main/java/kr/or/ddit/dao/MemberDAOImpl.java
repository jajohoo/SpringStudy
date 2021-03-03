package kr.or.ddit.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import kr.or.ddit.command.SearchCriteria;
import kr.or.ddit.dto.MemberVO;

public class MemberDAOImpl implements MemberDAO {

	// SqlSession
	private SqlSession sqlSession;// 가질 수 없읜 외부에서 줄거다
	
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	@Override
	public List<MemberVO> selectMemberList(SearchCriteria cri) throws SQLException {
		int offset = cri.getPageStartRowNum();
		int limit = cri.getPerPageNum();
		RowBounds rowBounds = new RowBounds(offset, limit);
		//페이지번호를 주면 행번호 알 수 있게 만들어 놓음 행번호 -1 *10하면 번호를 줬다 이말이다.
		
		List<MemberVO> memberList = null;
		
		//세션 가져올떄 하나짜리가있고 두개짜리는 cri를 파라미터 인자받아서 , 3개짜리는 rowBounds대로 자른다.
		// 몇개부터 몇개까지 페이지num , 몇행부터 몇행 이렇게 뿌린다, 0줄부터 0번지(선택해서 가져올때 번호를 붙인다.)
		
		memberList= sqlSession.selectList("Member-Mapper.selectSearchMemberList", cri, rowBounds);
				
				
		return memberList;
	}

	@Override
	public int selectMemberListCount(SearchCriteria cri) throws SQLException {
		int count = 0;
		count = sqlSession.selectOne("Member_Mapper.selectSearchMemberListCOunt",cri);
		return count;
	}

	@Override
	public MemberVO selectMemberById(String id) throws SQLException {
		MemberVO member = sqlSession.selectOne("Member-Mapper.selectMemberById", id);
		return member;
	}

	@Override
	public void insertMember(MemberVO member) throws SQLException {
		sqlSession.update("Member-Mapper.insertMember",member);
		
	}

	@Override
	public void updateMember(MemberVO member) throws SQLException {
		sqlSession.update("Member-Mapper.updateMember",member);

	}

	@Override
	public void deleteMember(String id) throws SQLException {
		sqlSession.update("Member-Mapper.deleteMember",id);

	}

	@Override
	public void disabledMember(String id) throws SQLException {
		sqlSession.update("Member-Mapper.disabledMember,id");

	}

	@Override
	public void enabledMember(String id) throws SQLException {
		sqlSession.update("Member-Mapper.enabledMember,id");

	}

}
