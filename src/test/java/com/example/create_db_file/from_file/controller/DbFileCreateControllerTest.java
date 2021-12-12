package com.example.create_db_file.from_file.controller;

import com.example.create_db_file.from_file.controller.form.OriginalDataFileForm;
import com.example.create_db_file.from_file.domain.service.DbFileCreateService;
import com.example.create_db_file.session.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("DbFileCreateControllerのテスト")
@SpringBootTest
class DbFileCreateControllerTest {

    private MockMvc mockMvc;

    private DbFileCreateController controller;

    @Mock
    private DbFileCreateService dbFileCreateService;

    @BeforeEach
    public void setUp() {
//        MockitoAnnotations.openMocks(this);
        controller = new DbFileCreateController(dbFileCreateService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                // viewResolversのセット これを設定しないとErrorになる
                .setViewResolvers(viewResolver())
                .build();
    }

    @Nested
    @DisplayName("getHomeのテスト")
    class getHomeTest {

        @DisplayName("普通にgetHomeにアクセスした場合のテスト")
        @Test
        void getHome() throws Exception {
            // homeにアクセス
            mockMvc.perform(get("/home"))
                    // レスポンスステータスが200であるかの検証
                    .andExpect(status().isOk())
                    // 返却がhome.htmlであるかの検証
                    .andExpect(view().name("home"))
                    // modelにkey:useInfo value:falseが設定されているかの検証
                    .andExpect(model().attribute("useInfo", false));
        }

        @DisplayName("パラメタにuseInfo=trueを設定した場合")
        @Test
        void getHomeParamOfUseInfoIsTrue() throws Exception {
            // homeにアクセス パラメータ設定
            mockMvc.perform(get("/home").param("useInfo", "true"))
                    .andExpect(status().is(200))
                    .andExpect(view().name("home"))
                    .andExpect(model().attribute("useInfo", true));

        }
    }

    @DisplayName("postUploadのテスト")
    @Nested
    class postUpload {
        @DisplayName("OriginalDataFileFormクラスのValidatorに引っかかるとhomeに遷移させる")
        @Test
        void postUploadHasValidatorError() throws Exception {
            OriginalDataFileForm form = new OriginalDataFileForm();
            mockMvc.perform(post("/upload")
                    .flashAttr("originalDataFileForm", form))
                    .andExpect(status().isOk())
                    .andExpect(view().name("home"))
                    .andExpect(model().hasErrors());
        }

        @DisplayName("DbCreateServiceのfindHeaderで空のMapが返却された場合homeに遷移する")
        @Test
        void postUploadOfFindHeaderIsEmpty() throws Exception {
            MultipartFile multipartFile = testMultipartFile("csv/employee.csv", "sample", "text/csv");
            OriginalDataFileForm form = new OriginalDataFileForm();
            form.setMultipartFile(multipartFile);

            doReturn(new HashMap<Integer, String>()).when(dbFileCreateService).findHeader(any(MultipartFile.class));

            mockMvc.perform(post("/upload")
                            .flashAttr("originalDataFileForm", form))
                    .andExpect(status().isOk())
                    // homeに遷移している
                    .andExpect(view().name("home"))
                    // 特殊なmodelが設定されている
                    .andExpect(model().attribute("noHeader", "※ヘッダーの値が読み取れないか、あるいはデータが情報が不足しています。ファイルを確認してください。"));
        }

        @DisplayName("sessionに値が格納されていない場合はsessionにファイルパスを保存しリダイレクトする事のテスト")
        @Test
        void postUploadFilePathAndSessionTest1() throws Exception{
            MultipartFile multipartFile = testMultipartFile("csv/employee.csv", "sample", "text/csv");
            OriginalDataFileForm form = new OriginalDataFileForm();
            form.setMultipartFile(multipartFile);
            // session生成
            UserSession userSession = new UserSession();

            doReturn(new HashMap<Integer, String>(){{ put(0, "sample");}}).when(dbFileCreateService).findHeader(any());
            doReturn("test/sample.csv").when(dbFileCreateService).fileToSaveTemporarily(any());

            MvcResult result =  mockMvc.perform(post("/upload")
                    .flashAttr("originalDataFileForm", form)
                    .sessionAttr("userSession", userSession))
                    // リダイレクトされているかの検査
                    .andExpect(status().isFound())
                    // リダイレクトもpathを検査
                    .andExpect(redirectedUrl("http://localhost/upload"))
                    .andReturn();

            UserSession actualSession =(UserSession) result.getModelAndView().getModel().get("userSession");
            // sessionにパスがちゃんと設定されているかの確認
            String expectedPath = "test/sample.csv";
            assertEquals(expectedPath, actualSession.getTemporalFilePath());
        }

        @DisplayName("sessionに値が格納されている場合はファイルを削除し新たにsessionにファイルパスを保存しリダイレクトする事のテスト")
        @Test
        void postUploadFilePathAndSessionTest2(@TempDir Path tempPath) throws Exception{
            MultipartFile multipartFile = testMultipartFile("csv/employee.csv", "sample", "text/csv");
            OriginalDataFileForm form = new OriginalDataFileForm();
            form.setMultipartFile(multipartFile);
            // session生成
            UserSession userSession = new UserSession();
            Path savePath = tempPath.resolve(Paths.get(tempPath.toString(), "sample.csv"));
            userSession.setTemporalFilePath(savePath.toString());

            Path newSavePath = Paths.get(tempPath.toString(), "sample2.csv");

            doReturn(new HashMap<Integer, String>(){{ put(0, "sample");}}).when(dbFileCreateService).findHeader(any());
            doReturn(newSavePath.toString()).when(dbFileCreateService).fileToSaveTemporarily(any());

            MvcResult result =  mockMvc.perform(post("/upload")
                    .flashAttr("originalDataFileForm", form)
                    .sessionAttr("userSession", userSession))
                    // リダイレクトされているかの検査
                    .andExpect(status().isFound())
                    // リダイレクトもpathを検査
                    .andExpect(redirectedUrl("http://localhost/upload"))
                    .andReturn();

            UserSession actualSession =(UserSession) result.getModelAndView().getModel().get("userSession");
            // sessionに新たなパスがちゃんと設定されているかの確認
            assertEquals(newSavePath.toString(), actualSession.getTemporalFilePath());
            // 古いファイルは削除されている事の確認
            assertFalse(Files.exists(savePath));
        }
    }

    private ViewResolver viewResolver()
    {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        viewResolver.setPrefix("classpath:templates/");
        viewResolver.setSuffix(".html");

        return viewResolver;
    }

    private static MockMultipartFile testMultipartFile(String sourcePath, String name, String contentType) throws IOException {
        Resource resource = new ClassPathResource(sourcePath);
        return new MockMultipartFile(name, resource.getFile().getName(), contentType, resource.getInputStream());
    }
}