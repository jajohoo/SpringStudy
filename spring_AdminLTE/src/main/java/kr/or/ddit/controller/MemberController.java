package kr.or.ddit.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.or.ddit.command.SearchCriteria;
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
	
	
}
