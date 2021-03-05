package kr.or.ddit.command;

import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.dto.MemberVO;

/**
 * @author pc17
 *
 */
public class MemberModifyCommand {
	//화면 중심으로 나오는거고 파일을 받는 picture은 multipartFile로 받는다
	//서블릿 -멀티파트 리졸버 화면단: enctype="file" 또뭐하나더있음
	
	private String id;	//아이디
    private String pwd; //패스워드
    private String name;//이름
    private String phone;//전화번호
    private String email;//이메일
    private String authority; //
    private MultipartFile picture; //사진파일
    private String oldPicture; //이전 사진파일명
    private String uploadPicture; //변경된 사진 파일명
    
    public MemberVO toParseMember() {
    	MemberVO member = new MemberVO();
    	member.setId(this.id);
    	member.setPwd(this.pwd);
    	member.setName(this.name);
    	member.setPhone(this.phone.replace("-", "")); // req param이라서 필터안먹어서 해줘야한다. trim도써주는게 좋다
    	member.setEmail(this.email);
    	member.setAuthority(this.authority);
    	
    	return member;
    }
    
    
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public MultipartFile getPicture() {
		return picture;
	}
	public void setPicture(MultipartFile picure) {
		this.picture = picure;
	}
	public String getUploadPicture() {
		return uploadPicture;
	}
	public void setUploadPicture(String uploadPicture) {
		this.uploadPicture = uploadPicture;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getOldPicture() {
		return oldPicture;
	}
	public void setOldPicture(String oldPicture) {
		this.oldPicture = oldPicture;
	}

    

}
