package com.mysite.crud2.controllers;

import com.mysite.crud2.models.Product;
import com.mysite.crud2.models.ProductDto;
import com.mysite.crud2.services.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository pRepo;

    @GetMapping({"","/"})
    public String showProductList(Model model) {
        List<Product> products = pRepo.findAll();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/createProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        if (productDto.getImageFile().isEmpty()) {
            //유저가 이미지를 입력하지 않고 전송시 BindingResult에 에러를 생서해 되돌아가게 함
            result.addError(new FieldError("productDto", "imageFile", "Image file is required"));
        }
        if (result.hasErrors()) {
            return "products/createProduct"; //되돌아감
        }
        //성공시 DB 저장
        //이미지 파일 이름 설정
        MultipartFile image = productDto.getImageFile();
        Date createDate = new Date();
        String storeFileName = createDate.getTime() + "_" + image.getOriginalFilename();
        //이미지를 public/images 폴더에 저장
        try {
            String uploadDir = "public/images/"; //저장 주소 문자열
            Path uploadPath = Paths.get(uploadDir); //업로드 주소 객체

            if (!Files.exists(uploadPath)) {
                Files.createDirectory(uploadPath); //만약 폴더가 없다며 만듬
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storeFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageFileName(storeFileName);

        pRepo.save(product); //이미지 파일은 public 폴더에 저장되고 DB에는 저장파일 이름을 저장

        return "redirect:/products";
    }

    //제품 수정 페이지
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = pRepo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return "products/editProduct";
    }

    //수정하기
    @PostMapping("/edit")
    public String editProduct(@Valid ProductDto productDto, BindingResult result,
                              @RequestParam int id,Model model) {

        Product product = pRepo.findById(id).get();
        model.addAttribute("product", product );
        //이미지가 없어도 에러발생 안함
        if (result.hasErrors()) {
            return "products/editProduct";
        }
        //수정할 이미지 있으면 기존 이미지 삭제하고 수정 이미지를 업로드함
        if (!productDto.getImageFile().isEmpty()) {
            String uploadDir = "public/images/";
            Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

            try{
                Files.delete(oldImagePath); //기존 이미지 삭제
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            //새 이미지 업로드
            MultipartFile image = productDto.getImageFile();
            Date createDate = new Date();
            String storeFileName = createDate.getTime() + "_" + image.getOriginalFilename();

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir+storeFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            product.setImageFileName(storeFileName); //이미지 파일 이름을 업데이트
        }
        //이미지 제외한 수정 내용을 업데이트
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        
        pRepo.save(product); //수정이 완료된 제품객체로 DB 업데이트함

        return "redirect:/products";
    }

    //삭제하기
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try{
            Product product = pRepo.findById(id).get();
            //이미지 파일 삭제하기
            String uploadDir = "public/images/";
            Path imagePath = Paths.get(uploadDir + product.getImageFileName());

            try{
                Files.delete(imagePath);
            }catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            //제품 삭제
            pRepo.delete(product);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return "redirect:/products";
    }
}
