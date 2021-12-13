package com.example.create_db_file.from_file.controller;

import com.example.create_db_file.from_file.controller.form.DBColumn;
import com.example.create_db_file.from_file.controller.form.OriginalDataFileForm;
import com.example.create_db_file.from_file.domain.service.DbFileCreateService;
import com.example.create_db_file.session.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.FlashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("DbFileCreateControllerのテスト")
@WebMvcTest(DbFileCreateController.class)
class DbFileCreateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DbFileCreateService dbFileCreateService;

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
                            .flashAttr("originalDataFileForm", form)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("home"))
                    .andExpect(model().hasErrors());
        }

        @DisplayName("DbCreateServiceのfindHeaderで空のMapが返却された場合homeに遷移する")
        @Test
        void postUploadOfFindHeaderIsEmpty() throws Exception {
            // setup
            MultipartFile multipartFile = testMultipartFile("csv/employee.csv", "sample", "text/csv");
            OriginalDataFileForm form = new OriginalDataFileForm();
            form.setMultipartFile(multipartFile);

            doReturn(new HashMap<Integer, String>()).when(dbFileCreateService).findHeader(any(MultipartFile.class));

            // 実行
            mockMvc.perform(post("/upload")
                            .flashAttr("originalDataFileForm", form)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isOk())
                    // homeに遷移している
                    .andExpect(view().name("home"))
                    // 特殊なmodelが設定されている
                    .andExpect(model().attribute("noHeader", "※ヘッダーの値が読み取れないか、あるいはデータが情報が不足しています。ファイルを確認してください。"));
        }

        @DisplayName("sessionに値が格納されていない場合はsessionにファイルパスを保存しリダイレクトする事のテスト")
        @Test
        void postUploadFilePathAndSessionTest1() throws Exception {
            // setup
            MultipartFile multipartFile = testMultipartFile("csv/employee.csv", "sample", "text/csv");
            OriginalDataFileForm form = new OriginalDataFileForm();
            form.setMultipartFile(multipartFile);
            // session生成
            UserSession userSession = new UserSession();

            doReturn(new HashMap<Integer, String>() {{
                put(0, "sample");
            }}).when(dbFileCreateService).findHeader(any());
            doReturn("test/sample.csv").when(dbFileCreateService).fileToSaveTemporarily(any());

            // 実行
            MvcResult result = mockMvc.perform(post("/upload")
                            .flashAttr("originalDataFileForm", form)
                            .flashAttr("userSession", userSession)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    // リダイレクトされているかの検査
                    .andExpect(status().isFound())
                    // リダイレクトもpathを検査
                    .andExpect(redirectedUrl("http://localhost/upload"))
                    .andReturn();

//            // sessionにパスがちゃんと設定されているかの確認
            assertEquals("test/sample.csv", userSession.getTemporalFilePath());
        }

        @DisplayName("sessionに値が格納されている場合はファイルを削除し新たにsessionにファイルパスを保存しリダイレクトする事のテスト")
        @Test
        void postUploadFilePathAndSessionTest2(@TempDir Path tempPath) throws Exception {
            // setup
            MultipartFile multipartFile = testMultipartFile("csv/employee.csv", "sample", "text/csv");
            OriginalDataFileForm form = new OriginalDataFileForm();
            form.setMultipartFile(multipartFile);
            // session生成
            UserSession userSession = new UserSession();
            Path savePath = tempPath.resolve(Paths.get(tempPath.toString(), "sample.csv"));
            userSession.setTemporalFilePath(savePath.toString());
            // 一時ファイル作成
            Files.createFile(savePath);

            Path newSavePath = Paths.get(tempPath.toString(), "sample2.csv");

            doReturn(new HashMap<Integer, String>() {{
                put(0, "sample");
            }}).when(dbFileCreateService).findHeader(any());
            doReturn(newSavePath.toString()).when(dbFileCreateService).fileToSaveTemporarily(any());

            // 実行
            MvcResult result = mockMvc.perform(post("/upload")
                            .flashAttr("originalDataFileForm", form)
                            .flashAttr("userSession", userSession)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    // リダイレクトされているかの検査
                    .andExpect(status().isFound())
                    // リダイレクトもpathを検査
                    .andExpect(redirectedUrlPattern("**/upload"))
                    .andReturn();

            // sessionに新たなパスがちゃんと設定されているかの確認
            assertEquals(newSavePath.toString(), userSession.getTemporalFilePath());
            // 古いファイルは削除されている事の確認
            assertFalse(Files.exists(savePath));
        }
    }

    @DisplayName("getUploadのテスト")
    @Nested
    class getUploadTest {
        @DisplayName("userSessionが設定されていない場合はhomeに戻る")
        @Test
        void getUploadNoneSessionValue() throws Exception {
            mockMvc.perform(get("/upload"))
                    .andExpect(view().name("home"));
        }

        @DisplayName("serviceのfindHeaderで空のMapが返却された場合はhomeに戻る")
        @Test
        void getUploadEmptyMap() throws Exception {
            // setup
            UserSession userSession = new UserSession();
            userSession.setTemporalFilePath("test.csv");

            doReturn(new HashMap<Integer, String>()).when(dbFileCreateService).findHeader(any());

            // 実行&検証
            MvcResult result = mockMvc.perform(
                            get("/upload")
                                    .flashAttr("userSession", userSession))
                    .andExpect(view().name("home"))
                    .andExpect(model().attribute("noHeader", "※予期しないエラーが発生しました。申し訳ありませんがやり直してください。"))
                    .andReturn();
            System.out.println(result.getModelAndView().getModel());
        }

        @DisplayName("正常に処理された場合はcustomページへ飛ぶ")
        @Test
        void getUploadRequestSuccess() throws Exception {
            // setup
            UserSession userSession = new UserSession();
            userSession.setTemporalFilePath("sample.csv");

            doReturn(new HashMap<Integer, String>() {{
                put(0, "sample");
            }}).when(dbFileCreateService).findHeader(any(),any());

            // 実行&検証
            MvcResult result = mockMvc.perform(
                            get("/upload")
                                    .flashAttr("userSession", userSession))
                    .andExpect(view().name("custom"))
                    .andExpect(model().attribute("columnType", DBColumn.ColumnType.values()))
                    .andReturn();
            System.out.println(result.getModelAndView().getModel());

        }
    }

    @DisplayName("endCreateのテスト")
    @Test
    void endCreateTest(@TempDir Path tempPath) throws Exception {
        // setup
        Path savePath = tempPath.resolve("sample.csv");
        // 一時ファイル作成
        Files.createFile(savePath);

        UserSession userSession = new UserSession();
        userSession.setTemporalFilePath(savePath.toString());

        // 実行＆検証
        MvcResult result = mockMvc.perform(
                post("/create")
                        .param("endCreate", "")
                        .flashAttr("userSession", userSession)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                // リダイレクトの検証
                .andExpect(status().isFound())
                // リダイレクト先Pathの確認
                .andExpect(redirectedUrlPattern("**/home"))
                .andReturn();

        // ファイルが削除されているかの検証
        assertFalse(Files.exists(savePath));

        FlashMap flashMap = result.getFlashMap();
        // フラッシュに期待した値が格納されているかの検証
        assertEquals("ご利用ありがとうございました!!", flashMap.get("message"));
    }

    @Nested
    @DisplayName("getSampleDataのテスト")
    class getSampleDataTest {
        @Test
        @DisplayName("test")
        void test() throws Exception {
            MvcResult result = mockMvc.perform(get("/getSample"))
                    .andExpect(status().isOk())
                    .andReturn();

        }
    }

    private static MockMultipartFile testMultipartFile(String sourcePath, String name, String contentType) throws IOException {
        Resource resource = new ClassPathResource(sourcePath);
        return new MockMultipartFile(name, resource.getFile().getName(), contentType, resource.getInputStream());
    }
}