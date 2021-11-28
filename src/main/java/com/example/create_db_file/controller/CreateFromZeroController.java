package com.example.create_db_file.controller;

import com.example.create_db_file.controller.create.ExcelFileView;
import com.example.create_db_file.controller.form.CreateFromZeroForm;
import com.example.create_db_file.controller.form.parts.*;
import com.example.create_db_file.controller.model.RequestJsonObj;
import com.example.create_db_file.controller.session.UserSession;
import com.example.create_db_file.domain.repository.DummyUserRepository;
import com.example.create_db_file.domain.service.CreateFromZeroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/from_zero")
@SessionAttributes(types = {UserSession.class, CreateFromZeroForm.class})
@RequiredArgsConstructor
public class CreateFromZeroController {

    private final RestTemplate restTemplate;

    private final DummyUserRepository dummyUserRepository;

    private final CreateFromZeroService createFromZeroService;

    @ModelAttribute("userSession")
    public UserSession userSession() {
        return new UserSession();
    }

    @ModelAttribute("createFromZeroForm")
    public CreateFromZeroForm createFromZeroForm() {
        return new CreateFromZeroForm();
    }

    @GetMapping("/form")
    public String getCreateFromZero(
            @ModelAttribute("createFromZeroForm") CreateFromZeroForm form,
            @ModelAttribute("addForm") AddForm addForm,
            Model model){
        model.addAttribute("nameTypes", NameType.values());
        model.addAttribute("numberTypes", NumberType.values());
        model.addAttribute("timeTypes", TimeType.values());
        model.addAttribute("stringTypes", StringType.values());

//        if(form.getFirstNameForms().isEmpty()){
//            form.addFirstNameForms();
//            form.addLastNameForms();
//        }

        return "create_and_custom";
    }

    @PostMapping(path = "/form", params = "add")
    public String postAdd(
            @ModelAttribute("createFromZeroForm") CreateFromZeroForm form,
            @ModelAttribute("addForm") AddForm addForm,
            Model model){

        addForm.addCreateZeroForm(form);
        addForm.initializeValue();

        model.addAttribute("nameTypes", NameType.values());
        model.addAttribute("numberTypes", NumberType.values());
        model.addAttribute("timeTypes", TimeType.values());
        model.addAttribute("stringTypes", StringType.values());
        return "create_and_custom";
    }

    @PostMapping(path = "/form", params = "create")
    public String postCreate(
            @Validated @ModelAttribute("createFromZeroForm") CreateFromZeroForm form,
            BindingResult bindingResult,
            @ModelAttribute("addForm") AddForm addForm,
            Model model){
        model.addAttribute("nameTypes", NameType.values());
        model.addAttribute("numberTypes", NumberType.values());
        model.addAttribute("timeTypes", TimeType.values());
        model.addAttribute("stringTypes", StringType.values());

        if (bindingResult.hasErrors()){
            return "create_and_custom";
        }
        String result = createFromZeroService.createInsertData(form);

        model.addAttribute("insertSentence", result);

        return "dataFileView";
    }

    @PostMapping(path = "/form", params = "create_excel")
    public ModelAndView postCreateExcel(
            @Validated @ModelAttribute("createFromZeroForm") CreateFromZeroForm form,
            BindingResult bindingResult,
            @ModelAttribute("addForm") AddForm addForm,
            Model model){
        ModelAndView mav = new ModelAndView();
        mav.addObject("nameTypes", NameType.values());
        mav.addObject("numberTypes", NumberType.values());
        mav.addObject("timeTypes", TimeType.values());
        mav.addObject("stringTypes", StringType.values());

        if (bindingResult.hasErrors()){
            mav.setViewName("create_and_custom");
            return mav;
        }
        List<List<String>> excelData = createFromZeroService.createExcelData(form);

        mav.addObject("excelData", excelData);
        mav.setView(new ExcelFileView());

        return mav;
    }

    @PostMapping(path = "/form", params = "remove")
    public String postRemove(
            @RequestParam String remove,
            @ModelAttribute("createFromZeroForm") CreateFromZeroForm form,
            @ModelAttribute("addForm") AddForm addForm,
            Model model){
        model.addAttribute("nameTypes", NameType.values());
        model.addAttribute("numberTypes", NumberType.values());
        model.addAttribute("timeTypes", TimeType.values());
        model.addAttribute("stringTypes", StringType.values());

        String[] removeItems = remove.split(",");
        form.removeField(removeItems[0], Integer.parseInt(removeItems[1]));
        return "create_and_custom";
    }

    @GetMapping("/test")
    public String getTest(){
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            String randStr = RandomString.make(rand.nextInt(7) + 5);
        }
        return "create_and_custom";
    }

    @GetMapping("/dummy/create")
    public String getDummyCreate(){

        String obj = restTemplate.getForObject("https://green.adam.ne.jp/roomazi/cgi-bin/randomname.cgi?n=200", String.class);
        int firstIndex = obj.indexOf("{");
        int lastIndex = obj.indexOf("}");

        String json = obj.substring(firstIndex,lastIndex + 1);
        ObjectMapper mapper = new ObjectMapper();

        try {
            RequestJsonObj jsonObj = mapper.readValue(json, RequestJsonObj.class);

            createFromZeroService.createDummyUsersByLists(jsonObj.getName());

            return "create_and_custom";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "create_and_custom";
        }

    }

}
