package com.example.create_db_file.inquiry.controller;

import com.example.create_db_file.inquiry.controller.form.InquiryCategory;
import com.example.create_db_file.inquiry.controller.form.InquiryForm;
import com.example.create_db_file.inquiry.domain.service.InquiryService;
import com.example.create_db_file.session.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@SessionAttributes(types = {UserSession.class})
@Controller
@RequestMapping("inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @ModelAttribute("userSession")
    public UserSession userSession() {
        return new UserSession();
    }

    @GetMapping
    public String getInquiry(
            @ModelAttribute("inquiryForm")InquiryForm form,
            Model model){
        model.addAttribute("categories", InquiryCategory.values());
        return "inquiry_form";
    }

    @PostMapping("request")
    public String postInquiryRequest(
            @Validated @ModelAttribute("inquiryForm") InquiryForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            UriComponentsBuilder builder
    ){
        if(bindingResult.hasErrors()){
            model.addAttribute("categories", InquiryCategory.values());
            return "inquiry_form";
        }

        inquiryService.saveInquiry(form.getContents(), form.getInquiryCategory().getIndex());

        String redirect = MvcUriComponentsBuilder.relativeTo(builder)
                .withMappingName("IC#getInquiry").encode().build();
        redirectAttributes.addFlashAttribute("complete", "ご意見・ご質問の方頂きありがとうございます!!");
        return "redirect:" + redirect;
    }
}
