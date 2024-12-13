package com.mysite.crud2.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
//유저에게 제품 입력을 받는 객체
public class ProductDto {

    @NotEmpty(message = "이름을 입력하세요")
    private String name;
    @NotEmpty(message = "브랜드를 입력하세요")
    private String brand;
    @NotEmpty(message = "카테고리를 입력하세요")
    private String category;
    @Min(value = 0, message = "최솟값은 0이상 입니다")
    private int price;

    @Size(min = 10, message = "제품설명은 10자 이상이여야 합니다")
    @Size(max = 100, message = "제품설명은 100자 이하여야 합니다")
    private String description;

    //날짜는 현재날짜시간으로 자동입력
    //DB에는 파일의 이름만 저장되지만 실제 유저로부터 파일이미지를 받음
    private MultipartFile imageFile;

}
