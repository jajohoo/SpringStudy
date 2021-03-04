package kr.or.ddit.service;

import java.sql.SQLException;
import java.util.List;

import kr.or.ddit.dao.MenuDAO;
import kr.or.ddit.dto.MenuVO;

public class MenuServiceImpl implements MenuService {

	
	
	//무조건 받아서 쓰게만들어야한다.
	//데이터 가공시 쓰게만듬
	private MenuDAO menuDAO;
	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	@Override
	public List<MenuVO> getMainMenuList() throws SQLException {
		List<MenuVO> menuList = null;
		
		menuList = menuDAO.selectMainMenu();

		return menuList;
	}

	@Override
	public List<MenuVO> getSubMenuList(String mCode) throws SQLException {
		List<MenuVO> menuList = null;

		menuList = menuDAO.selectSubMenu( mCode);
	
		return menuList;
	}

	@Override
	public MenuVO getMenuByMcode(String mCode) throws SQLException {
		MenuVO menu = null;

		menu = menuDAO.selectMenuByMcode(mCode);
		
		return menu;
	}

}
