package kr.or.ddit.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.or.ddit.command.MemberModifyCommand;
import kr.or.ddit.command.MemberRegistCommand;
import kr.or.ddit.command.SearchCriteria;
import kr.or.ddit.dto.MemberVO;
import kr.or.ddit.service.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("/main")
	public void main() {}
	//adapter가 파라미터를 받아서 하면 내보내지 않는 한 리스폰스도 안보낸다
	//화면결정자인 httpServlet이 없다면 파라미터도 없다
	
	@RequestMapping("/list")
	public ModelAndView list(SearchCriteria cri, ModelAndView mnv) throws SQLException{
	//변수명 맞추는 거 꼭 기억해라 파라미터랑 변수명 
	//집합체 ref가 나옴면 파라미터이름에 set을 붙여서 invoke한다.
	//한번에 통째로 넣고 resolver로 하나씩 뽑아준다.
		String url = "member/list";
		
		Map<String, Object> dataMap = memberService.getSearchMemberList(cri);
		//맵을 넣어줬지만 리졸버가 req에 하나씩 key값으로 심어준다
		//꺼낼떄는 pageMaker랑 memberList로 한다.
		mnv.addAllObjects(dataMap);
		mnv.setViewName(url);
		
		return mnv;
	}
	
	@RequestMapping(value="/registForm",method=RequestMethod.GET)
	public String registForm() {
		String url="member/regist";
		return url;
	}
	
	
	//Resource는 이름으로 땡긴다. autowired는 upload.properties에 3개나 있어서
	//구분이 안된다 그래서 이렇게 개별의 하나만 데려올 수 있다.
	@Resource(name = "picturePath")
	private String picturePath;
	
	private String savePicture(String oldPicture, MultipartFile multi) throws Exception{
		String fileName = null;
		
		/* 파일 유무확인 */
		if (!(multi == null || multi.isEmpty() || multi.getSize() > 1024 * 1024 * 5)) {
			/* 파일저장폴더 설정 */
			String uploadPath = picturePath;
			fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
			File storeFile = new File(uploadPath, fileName);
			
			storeFile.mkdir();
			
			// local HDD 에 저장.
			multi.transferTo(storeFile);
			
			if(!oldPicture.isEmpty()) {
				File oldFile = new File(uploadPath, oldPicture);
				if(oldFile.exists()) {
					oldFile.delete();
				}
			}
		}
		return fileName;
	}
	
	//업로드 전에 폼태그만들고 업로드 버튼누르면 따로 만든 폼태그의 input태그 작동
	//그래서 그쪽의 폼태그의 input태그에 file이 들어가서 ajax로 이걸 던져서 미리보기됨
	//사용자가 이미지 업로드를 다시해서 업로드가 완성되면 oldpicture에 다시한번 쓴다.
	//그 다음파일에 쓸때는 이전파일로 들어간다.
	@RequestMapping(value="/picture",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> picture(@RequestParam("pictureFile") MultipartFile multi, String oldPicture) throws Exception{
		//entity 에 파일명을 넣어준다.
		ResponseEntity<String> entity = null;
		
		String result = "";
		HttpStatus status = null;
		
		/* 파일저장확인 */
		if ((result = savePicture(oldPicture, multi)) == null) {
			result = "업로드 실패했습니다.!";
			status = HttpStatus.BAD_REQUEST;
		} else {
			status = HttpStatus.OK;
		}
		
		entity = new ResponseEntity<String>(result, status);
		
		return entity;
	}
	
	// method = RequestMethod.POST 해줘야지 url의 파일의 한글이 안깨지도록 해줌 그리고 server의 encoding 설정
	@RequestMapping(value="/getPicture", produces = "text/plain;charset=utf-8")
	@ResponseBody
	public ResponseEntity<byte[]> getPicture(String picture) throws Exception{
		InputStream in = null;
		ResponseEntity<byte[]> entity = null;
		String imgPath = this.picturePath;
		try {
			
			// in = new FileInputStream(imgPath+File.separator+picture);
			in = new FileInputStream(new File(imgPath, picture));
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), HttpStatus.CREATED);
			
		}catch(IOException e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			in.close();
		}
		return entity;
		
		
	}
	
	// 아이디 확인 근데 바로 바디로 표출하니까 ResponseEntity<> 사용함
	@RequestMapping("/idCheck")
	@ResponseBody
	public ResponseEntity<String> idCheck(String id) throws Exception{
		ResponseEntity<String> entity = null;
		
		try {
			MemberVO member = memberService.getMember(id);
			if(member != null) {
				entity = new ResponseEntity<String>("duplicated",HttpStatus.OK);
				
			}else {
				entity = new ResponseEntity<String>("",HttpStatus.OK);
				
			}
			
		}catch(SQLException e) {
			entity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	}
	
	@RequestMapping(value="/regist",method= RequestMethod.POST)
	public void regist(MemberRegistCommand memberReq, HttpServletRequest request, HttpServletResponse response)throws SQLException, IOException{
		//phone 과 ㄴ등록 날짜때문에 인풋이 여러개 때문에 memberVO를 제대로 못 가져오기떄문에
		//memberVO를 직접적으로 가져올 수 없다.
		//MemberRegistCommand 파라미터를 memberReq로 받아와서 못받는 VO를 서비스로 보내주게 해준다.
		MemberVO member = memberReq.toMemberVO();
		memberService.regist(member);
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('회원등록이 정상적으로 되었습니다.');");
		out.println("window.opener.location.href='" + request.getContextPath() + "/member/list.do';");
		out.println("window.close();");
		out.println("</script>");
		
		if (out != null)
			out.close();
	}
	
	//상세페이지
	@RequestMapping(value="/detail", method= RequestMethod.GET)
	public String detail(String id, Model model) throws SQLException{
		String url = "member/detail";
		
		MemberVO member = memberService.getMember(id);
		model.addAttribute("member",member);
		 
		return url;
		
	}
	
	//수정
	@RequestMapping(value="/modify", method= RequestMethod.GET)
	public String modify(String id, Model model) throws SQLException{
		String url = "member/modify";
		
		MemberVO member = memberService.getMember(id);
		model.addAttribute("member",member);
		
		return url;
		
	}
	
	@RequestMapping(value="/modify", method= RequestMethod.POST)
	public void modify(MemberModifyCommand modifyReq, HttpSession session, HttpServletResponse response) throws Exception{
		MemberVO member = modifyReq.toParseMember();
		
		//사진을 수정안할 시 null이 나옴
		//신규 파일 변경 및 기존 파일 삭제
		String fileName = savePicture(modifyReq.getOldpicture(), modifyReq.getPicture());
		member.setPicture(fileName);
		
		//파일변경 없을 시 기존 파일명 유지
		if(modifyReq.getPicture().isEmpty()) {
			member.setPicture(modifyReq.getOldpicture());
		}
		
		//DB 내용수정
		memberService.modify(member);
		
		//로그인한 사용자의 경우 수정된 정보로 session 업로드
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		if(loginUser != null && member.getId().equals(loginUser.getId())) {
			session.setAttribute("loginUser", member);
		}
		
		response.setContentType("text/htmlcharset=utf-8");
		PrintWriter out = response.getWriter();
		String output = ""+"<script>"+"alert('수정되었습니다.');"+"location.href='detail?id="
						+ member.getId()+ "';"
						+ "window.opener.parent.location.reload();"+"</script>";
		out.println(output);
		out.close();
	}
	
	@RequestMapping(value="/remove", method= RequestMethod.GET)
	public String remove(String id, HttpSession session, Model model) throws SQLException{
		String url = "member/removeSuccess";
		
		MemberVO member;
		
		//이미지파일을 삭제
		member = memberService.getMember(id);
		
		String svaePath = this.picturePath;
		File imageFile = new File(svaePath, member.getPicture());
		if(imageFile.exists()) {
			imageFile.delete();
		}
		
		memberService.remove(id);
		
		//삭제되는 회원이 로그인 회원일 경우 로그아웃 해야함
		MemberVO loginUser = (MemberVO) session.getAttribute("loginUser");
		if(loginUser.getId().equals(member.getId())) {
			session.invalidate();
		}
		
		model.addAttribute("member",member);
		return url;
		
	}
}
