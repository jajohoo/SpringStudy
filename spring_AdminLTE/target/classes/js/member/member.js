function Preview_picture(fileName){
	var imageURL = "getPicture.do?picture="+fileName;
	$('div#pictureView').css({'background-image':'url('+imageURL+')',
							  'background-position':'center', 	
							  'background-size':'center', 	
							  'background-repeat':'no-repeat'
								});
	
}

