package kr.or.ddit.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.or.ddit.dto.MenuVO;
import kr.or.ddit.exception.InvalidPasswordException;
import kr.or.ddit.exception.NotFoundIDException;
import kr.or.ddit.service.MemberService;
import kr.or.ddit.service.MenuService;

@Controller
public class CommonController {
	//떙겨올때 쓰는거
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping(value="/common/loginForm",method=RequestMethod.GET)
	public String loginForm() {
		String url = "common/loginForm";
		return url;
	}
	
	//invoke는 해당타입과 변수명을 가져온다. 
	@RequestMapping(value="/common/login",method=RequestMethod.POST)
	public String login(String id, String pwd, HttpSession session) throws SQLException
	{
		//파라미터도 파싱해서 가져온다 그럼 id라는 req파라미터를 int로 바꾸려고한다. 그럼 parsing error뜬다
		//adapter는 exception 방출x => 400에러 뜬다.
		//대부분 Date떄문에 터진다 이 점 유의하자
		//이제 파라미터와 변수명을 일치시켜주자
		String url = "redirect:/index.do";
		
		try {
			memberService.login(id, pwd, session);
		}catch(NotFoundIDException | InvalidPasswordException e) {
			url="redirect:/";
			session.setAttribute("msg", e.getMessage());
		}
		
		return url;
	}
	
	@RequestMapping(value="/common/logout", method=RequestMethod.GET)
	public String logout(HttpSession session) {
		//redirect에 contextPath를 주기위해 request 파라미터로 주고 준다
		String url = "redirect:/";
		session.invalidate();
		
		return url;
	}
	
	
	
	
	//requestParamAnnotation을 가지고 가는데 이게 해당포인트 null값 가져간다.
	//부적합한 열유형은 mapper에 null이 들어가면 그렇게 뜬다.
	//service는 null을 판단x dao로 보내고 dao도 판단 없이 또 보낸다.
	
	//주소값이 달라서 둘다 바꾸기 힘들때!!!!!!
	//넘어오는 파라미터 이름이 mCode와 다를시 mybatis처럼 map만들어주는게 아니라
	//바로 name="mmCode" 로 주면 어댑터가 읽어서 mmCode라는 파라미터를 리퀘스트에서 뽑는다
	//있으면 주고 없으면? defaultValue를 준다.
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public ModelAndView index(@RequestParam(defaultValue="M000000")String mCode, ModelAndView mnv)throws SQLException {
		String url = "/common/indexPage";
		
		List<MenuVO> menuList = menuService.getMainMenuList();
		MenuVO menu = menuService.getMenuByMcode(mCode);
		
		mnv.addObject("menuList",menuList);
		mnv.addObject("menu",menu);
		mnv.setViewName(url);
		
		return mnv;
	}
	
	@RequestMapping(value="/common/subMenu",method=RequestMethod.GET)
	   @ResponseBody
	   public ResponseEntity<List<MenuVO>> subMenu(String mCode) {
		//얘는 view로 보내는게 아니라 그냥 내보내는것이다 라는표시 ResponseBody
				//객체를 Serialize하는 표시
	      ResponseEntity<List<MenuVO>> entity = null;
	      
	      try {
	         List<MenuVO> subMenu = menuService.getSubMenuList(mCode);
	         entity = new ResponseEntity<List<MenuVO>>(subMenu,HttpStatus.OK);
	      }catch(SQLException e) {
	         entity = new ResponseEntity<List<MenuVO>>(HttpStatus.INTERNAL_SERVER_ERROR);
	      }
	      
	      return entity;
	   }
	
	@RequestMapping("/home")
	public String main() {
		String url = "/common/home";
		return url;
	}
}
