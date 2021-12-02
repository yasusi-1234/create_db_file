package com.example.create_db_file.from_zero.domain.service;

import com.example.create_db_file.from_zero.controller.form.CreateFromZeroForm;
import com.example.create_db_file.from_zero.domain.model.DummyUserFirstName;
import com.example.create_db_file.from_zero.domain.model.DummyUserLastName;
import com.example.create_db_file.from_zero.domain.repository.DummyUserFirstNameRepository;
import com.example.create_db_file.from_zero.domain.repository.DummyUserLastNameRepository;
import com.example.create_db_file.from_zero.domain.model.mapper.TemporaryDummyUser;
import com.example.create_db_file.from_zero.controller.form.parts.FirstNameForm;
import com.example.create_db_file.from_zero.controller.form.parts.LastNameForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CreateFromZeroServiceImpl implements CreateFromZeroService {

    private final DummyUserFirstNameRepository dummyUserFirstNameRepository;

    private final DummyUserLastNameRepository dummyUserLastNameRepository;

    /**
     * List<List<String>>型のデータを受け取り、DummyUser形にそれぞれ変換しデータベースに登録する
     *
     * @param requestUsers ユーザー情報が格納されたデータ
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void createDummyUsersByLists(List<List<String>> requestUsers) {

        List<DummyUserFirstName> firstNames = new ArrayList<>();
        List<DummyUserLastName> lastNames = new ArrayList<>();

        List<DummyUserFirstName> existsFirstNames = dummyUserFirstNameRepository.findAll();
        List<DummyUserLastName> existsLastNames = dummyUserLastNameRepository.findAll();

        requestUsers.forEach(data -> {
            String[] fullName = data.get(0).split(" ");
            String[] fullNameKana = data.get(1).split(" ");
            String[] fullNameRoman = data.get(2).split(" ");

            String firstName = fullName[1];
            String lastName = fullName[0];
            String firstNameKana = fullNameKana[1];
            String lastNameKana = fullNameKana[0];
            String firstNameRoman = fullNameRoman[1].toLowerCase();
            String lastNameRoman = fullNameRoman[0].toLowerCase();

            boolean existFirst = existsFirstNames.stream().map(DummyUserFirstName::getFirstName).anyMatch(f -> f.equals(firstName));
            boolean existLast = existsLastNames.stream().map(DummyUserLastName::getLastName).anyMatch(f -> f.equals(lastName));

            if(!existFirst){
                DummyUserFirstName userFirstName = DummyUserFirstName.of(firstName, firstNameKana, firstNameRoman);
                firstNames.add(userFirstName);
            }

            if(!existLast){
                DummyUserLastName userLastName = DummyUserLastName.of(lastName, lastNameKana, lastNameRoman);
                lastNames.add(userLastName);
            }

        });

        List<DummyUserFirstName> savedFirstNames = dummyUserFirstNameRepository.saveAll(firstNames);
        List<DummyUserLastName> savedLastNames = dummyUserLastNameRepository.saveAll(lastNames);
        log.info("新しいダミーユーザーを登録しました。 firstNameの登録件数: {}, lastNameの登録数: {}", savedFirstNames.size(), savedLastNames.size());

    }

    /**
     * フォーム情報に応じたInsert文を生成し返却する
     * @param form フォーム情報
     * @return フォーム情報に応じたInsert文
     */
    @Override
    public String createInsertData(CreateFromZeroForm form){

        int createSize = form.getCreateSize();

        String insertLeft = form.createInsertHeaderLeftSide();

        List<FirstNameForm> firstNameForms = form.getFirstNameForms();
        List<LastNameForm> lastNameForms = form.getLastNameForms();
        boolean noNameData = CollectionUtils.isEmpty(firstNameForms) && CollectionUtils.isEmpty(lastNameForms);
        List<TemporaryDummyUser> temporaryDummyUsers = noNameData ? null : createTemporaryDummyUser(createSize);

        setFormData(form, createSize);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < createSize; i++) {
            sb.append(insertLeft);

            final int in = i;
            if (!noNameData){
                TemporaryDummyUser tdu = temporaryDummyUsers.get(in);
                lastNameForms.forEach(last -> sb.append(last.getInsertString(tdu)).append(", "));
                firstNameForms.forEach(first -> sb.append(first.getInsertString(tdu)).append(", "));
            }
            form.getMailAddressForms().forEach(mail -> sb.append(mail.getInsertMailAddress(in)).append(", "));
            form.getNumberForms().forEach(num -> sb.append(num.getInsertNumber(in)).append(", "));
            form.getStringDataForms().forEach(str -> sb.append(str.getInsertString(in)).append(", "));
            form.getDateTimeForms().forEach(dt -> sb.append(dt.getInsertLocaleDateTime(in)).append(", "));
            form.getDateForms().forEach(d -> sb.append(d.getInsertDateTime(in)).append(", "));
            form.getTimeForms().forEach(t -> sb.append(t.getInsertDateTime(in)).append(", "));

            sb.setLength(sb.length() - 2);
            sb.append(");").append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public List<List<String>> createExcelData(CreateFromZeroForm form){

        int createSize = form.getCreateSize();

        List<FirstNameForm> firstNameForms = form.getFirstNameForms();
        List<LastNameForm> lastNameForms = form.getLastNameForms();
        boolean noNameData = CollectionUtils.isEmpty(firstNameForms) && CollectionUtils.isEmpty(lastNameForms);
        List<TemporaryDummyUser> temporaryDummyUsers = noNameData ? null : createTemporaryDummyUser(createSize);

        setFormData(form, createSize);

        List<List<String>> createExcelData = new ArrayList<>();
        
        createExcelData.add(form.createHeaderList());
        
        for (int i = 0; i < createSize; i++) {
            
            List<String> dataList = new ArrayList<>();
            
            final int in = i;
            if (!noNameData){
                TemporaryDummyUser tdu = temporaryDummyUsers.get(in);
                lastNameForms.forEach(last -> dataList.add(last.getDataString(tdu)));
                firstNameForms.forEach(first -> dataList.add( first.getDataString(tdu)));
            }
            form.getMailAddressForms().forEach(mail -> dataList.add( mail.getDataMailAddress(in)));
            form.getNumberForms().forEach(num -> dataList.add( num.getInsertNumber(in)));
            form.getStringDataForms().forEach(str -> dataList.add( str.getDataString(in)));
            form.getDateTimeForms().forEach(dt -> dataList.add( dt.getDataLocaleDateTime(in)));
            form.getDateForms().forEach(d -> dataList.add( d.getDataDateTime(in)));
            form.getTimeForms().forEach(t -> dataList.add( t.getDataDateTime(in)));

            createExcelData.add(dataList);
        }

        return createExcelData;
    }

    /**
     * フォームの内部のそれぞれのフォーム情報に作成用データをセットする
     * @param form フォームデータ
     * @param createSize 作成数
     */
    private void setFormData(CreateFromZeroForm form, int createSize){
        setMailAddress(form, createSize);
        setNumbers(form, createSize);
        setStrings(form, createSize);
        setDateTime(form, createSize);
        setDate(form, createSize);
        setTime(form, createSize);
    }

    /**
     * フォームのNumberFormの情報からそれぞれのNumberTypeに合致する数値リスト情報をセットする
     * @param form フォーム情報
     * @param createSize 作成数
     */
    private void setNumbers(CreateFromZeroForm form,int createSize){
        form.getNumberForms().forEach(number -> {
            List<Long> numbers;
            switch (number.getNumberType()){
                case Between:
                    numbers = createRandomNumber(number.getMinNumber(), number.getMaxNumber(), createSize);
                    number.setNumbers(numbers);
                    break;
                case Rand:
                    numbers = createRandomNumber(createSize);
                    number.setNumbers(numbers);
                    break;
                case Serial:
                    numbers = createSerialNumber(number.getMinNumber(), createSize);
                    number.setNumbers(numbers);
                    break;
            }
        });
    }

    /**
     * 開始数から1づつIncrementした値を指定された数分作成し返却するメソッド
     * @param startNum 開始の数値
     * @param createSize 作成数
     * @return 開始数から1づつIncrementしたリスト
     */
    private List<Long> createSerialNumber(long startNum, int createSize){
        return Stream.iterate(startNum, l -> l + 1).limit(createSize).collect(Collectors.toList());
    }

    /**
     * ランダムに数値を生成し指定された数分のリストに格納し返却する
     * @param createSize 作成数
     * @return ランダムに生成された数値リスト
     */
    private List<Long> createRandomNumber(int createSize){
        List<Long> numbers = new ArrayList<>();
        for (int i = 0; i < createSize; i++) {
            numbers.add(ThreadLocalRandom.current().nextLong(0, Integer.MAX_VALUE));
        }
        return numbers;
    }

    /**
     * 最小値と最大値の情報からランダムな数値を生成し、それを指定された数分生成しリスト形式で返却する
     * @param startNum 最小値の数値
     * @param endNum 最大値の数値
     * @param createSize 作成数
     * @return 最小値と最大値の情報から作成したランダムに生成されたリスト情報
     */
    private List<Long> createRandomNumber(long startNum, long endNum, int createSize){
        List<Long> numbers = new ArrayList<>();
        for (int i = 0; i < createSize; i++) {
            numbers.add(ThreadLocalRandom.current().nextLong(startNum, endNum + 1));
        }
        return numbers;
    }

    /**
     * フォーム情報のStringDataFormsの情報を元に合致するデータをセットする
     * @param form フォーム
     * @param createSize 作成数
     */
    private void setStrings(CreateFromZeroForm form, int createSize){
        form.getStringDataForms().forEach(str -> {
            List<String> strings;
            switch (str.getStringType()){
                case Between:
                    strings = createBetweenStrings(str.getMinLength(), str.getMaxLength(), createSize);
                    str.setStringList(strings);
                    break;
                case Rand:
                    strings = createRandomStrings(createSize);
                    str.setStringList(strings);
                    break;
                case UUID:
                    strings = createUUID(createSize);
                    str.setStringList(strings);
            }
        });
    }

    /**
     * 指定された作成分の5~20文字のランダムな文字列を生成し、リスト形式で返却する
     * @param createSize 作成数
     * @return 指定された作成分の5~20文字のランダムな文字列のリスト
     */
    private List<String> createRandomStrings(int createSize){
        return createBetweenStrings(5, 20, createSize);
    }

    /**
     * 最小値から最大値の間のランダムな文字列を作成数分生成し返却する
     * @param minLength 最小値の値
     * @param maxLength 最大値の値
     * @param createSize 作成数
     * @return 最小値から最大値の間のランダムな文字列のリスト
     */
    private List<String> createBetweenStrings(int minLength, int maxLength, int createSize){
        List<String> strings = new ArrayList<>();

        if (maxLength > 100){
            maxLength = 100;
        }
        for (int i = 0; i < createSize; i++) {
            String randStr = RandomString.make(ThreadLocalRandom.current().nextInt(minLength, maxLength + 1));
            strings.add(randStr);
        }
        return strings;
    }

    /**
     * 指定された数分のUUIDをリスト形式で返却する
     * @param createSize 作成数
     * @return 指定された数分のUUIDのリスト
     */
    private List<String> createUUID(int createSize){
        List<String> uuidList = new ArrayList<>();
        for (int i = 0; i < createSize; i++) {
            uuidList.add(UUID.randomUUID().toString());
        }
        return uuidList;
    }

    /**
     * フォーム情報のDateTimeFormの情報を元に合致するデータをセットする
     * @param form フォーム
     * @param createSize 作成数
     */
    private void setDateTime(CreateFromZeroForm form, int createSize){
        form.getDateTimeForms().forEach(dateTime -> {
            List<LocalDateTime> localDateTimes;
            switch (dateTime.getTimeType()){
                case Between:
                    localDateTimes = createRandomLocalDateTime(dateTime.getMinTime(), dateTime.getMaxTime(), createSize);
                    dateTime.setLocalDateTimes(localDateTimes);
                    break;
                case Rand:
                    localDateTimes = createRandomLocalDateTime(createSize);
                    dateTime.setLocalDateTimes(localDateTimes);
            }
        });
    }

    /**
     * エポック時間から現在の日付情報までのデータからランダムに生成されたリストを返却する
     * @param createSize 作成数
     * @return エポック時間から現在の日付情報からランダムに生成されたリスト
     */
    private List<LocalDateTime> createRandomLocalDateTime(int createSize){
        LocalDateTime min = LocalDateTime.ofEpochSecond(0,0, ZoneOffset.UTC);
        LocalDateTime max = LocalDateTime.now();
        return createRandomLocalDateTime(min, max, createSize);
    }

    /**
     * 引数で指定されたstartとendの情報の間のLocalDateTimeのデータをランダムに生成し返却する
     *
     * @param start 開始の年月日時分秒(LocalDateTime)情報
     * @param end 終了の年月日時分秒(LocalDateTime)情報
     * @param createSize 作成数
     * @return ランダムに生成したLocalDateTimeの情報リスト
     */
    private List<LocalDateTime> createRandomLocalDateTime(LocalDateTime start, LocalDateTime end, int createSize){
        LocalDate startLocalDate = start.toLocalDate(); 
        LocalTime startLocalTime = start.toLocalTime();
        long lsld = startLocalDate.toEpochDay();
        int islt = startLocalTime.toSecondOfDay();

        LocalDate endLocalDate = end.toLocalDate().plusDays(1);
        LocalTime endLocalTime = end.toLocalTime();
        long leld = endLocalDate.toEpochDay();
        int ielt = endLocalTime.toSecondOfDay();

        List<LocalDateTime> createLocalDateList = new ArrayList<>();

        for (int i = 0; i < createSize; i++) {
            long longDate = ThreadLocalRandom.current().nextLong(lsld, leld);
            int intTime;

            if(longDate == lsld){
                intTime = ThreadLocalRandom.current().nextInt(islt, LocalTime.MAX.toSecondOfDay());
            }else if (longDate == leld){
                intTime = ThreadLocalRandom.current().nextInt(LocalTime.MIN.toSecondOfDay(), ielt);
            }else{
                intTime = ThreadLocalRandom.current().nextInt(LocalTime.MIN.toSecondOfDay(), LocalTime.MAX.toSecondOfDay());
            }
            createLocalDateList.add(LocalDateTime.of(LocalDate.ofEpochDay(longDate), LocalTime.ofSecondOfDay(intTime)));
        }

        return createLocalDateList;
    }

    /**
     * フォーム情報のDateFormの情報を元に合致するデータをセットする
     * @param form フォーム
     * @param createSize 作成数
     */
    private void setDate(CreateFromZeroForm form, int createSize){
        form.getDateForms().forEach(date -> {
            List<LocalDate> dateTimes;
            switch (date.getTimeType()){
                case Between:
                    dateTimes = createRandomLocalDate(date.getMinTime(), date.getMaxTime(), createSize);
                    date.setLocalDates(dateTimes);
                    break;
                case Rand:
                    dateTimes = createRandomLocalDate(createSize);
                    date.setLocalDates(dateTimes);
            }
        });
    }

    /**
     * エポック時間と現在の年月日の間からランダムな年月日情報を生成するメソッド
     * @param createSize 作成数
     * @return エポック時間と現在の年月日の間から生成されたランダムな年月日リスト
     */
    private List<LocalDate> createRandomLocalDate(int createSize){
        LocalDate start = LocalDate.ofEpochDay(0);
        LocalDate end = LocalDate.now().plusDays(1);

        return createRandomLocalDate(start, end, createSize);
    }

    /**
     * 開始日、終了日の情報を元にランダムに日付情報を生成し返却するメソッド
     * @param start 開始の年月日(LocalDate)
     * @param end 開始の年月日(LocalDate)
     * @param createSize 作成数
     * @return 引数startとendの情報から作成されたランダムな日付情報リスト
     */
    private List<LocalDate> createRandomLocalDate(LocalDate start, LocalDate end, int createSize){
        long startLong = start.toEpochDay();
        long endLong = end.plusDays(1).toEpochDay();

        List<LocalDate> localDates = new ArrayList<>();

        for (int i = 0; i < createSize; i++) {
            long randomLong = ThreadLocalRandom.current().nextLong(startLong, endLong);
            localDates.add(LocalDate.ofEpochDay(randomLong));
        }
        return localDates;
    }

    /**
     * フォーム情報のTimeFormの情報を元に合致するデータをセットする
     * @param form フォーム
     * @param createSize 作成数
     */
    private void setTime(CreateFromZeroForm form, int createSize){
        form.getTimeForms().forEach(time -> {
            List<LocalTime> times;
            switch (time.getTimeType()){
                case Between:
                    times = createRandomLocalTime(time.getMinTime(), time.getMaxTime(), createSize);
                    time.setLocalTimes(times);
                    break;
                case Rand:
                    times = createRandomLocalTime(createSize);
                    time.setLocalTimes(times);
            }
        });
    }

    /**
     * 00:00:00から23:59:59までの時分秒をランダムに生成しリスト形式で返却する
     * @param createSize 作成数
     * @return 00:00:00から23:59:59までの時分秒からランダムに生成されたリスト
     */
    private List<LocalTime> createRandomLocalTime(int createSize){
        return createRandomLocalTime(LocalTime.MIN, LocalTime.MAX, createSize);
    }

    /**
     * 引数startとendの間のデータからランダムに時分秒リストを生成し返却する
     * @param start 開始時分秒
     * @param end 終了時分秒
     * @param createSize 作成数
     * @return 引数startとendの間のデータからランダムに生成された時分秒リスト
     */
    private List<LocalTime> createRandomLocalTime(LocalTime start, LocalTime end, int createSize){
        int startTime = start.toSecondOfDay();
        int endTime = end.toSecondOfDay() + 1;

        List<LocalTime> localTimes = new ArrayList<>();

        for (int i = 0; i < createSize; i++) {
            int randomTime = ThreadLocalRandom.current().nextInt(startTime, endTime);
            localTimes.add(LocalTime.ofSecondOfDay(randomTime));
        }
        return localTimes;
    }

    /**
     * 引数で指定されたサイズを元にランダムにユーザー情報を生成し返却する
     * @param createSize 作成数
     * @return ランダムに生成したユーザー情報のリスト
     */
    private List<TemporaryDummyUser> createTemporaryDummyUser(int createSize){
        List<DummyUserFirstName> firstNames = dummyUserFirstNameRepository.findAll();
        int firstSize = firstNames.size();
        List<DummyUserLastName> lastNames = dummyUserLastNameRepository.findAll();
        int lastSize = lastNames.size();

        List<TemporaryDummyUser> temporaryDummyUsers = new ArrayList<>();

        Random rand = new Random();

        for (int i = 0; i < createSize; i++) {
            DummyUserFirstName first = firstNames.get(rand.nextInt(firstSize));
            DummyUserLastName last = lastNames.get(rand.nextInt(lastSize));
            temporaryDummyUsers.add(
                    TemporaryDummyUser.of(
                            first.getFirstName(),last.getLastName(),
                            first.getFirstNameKana(), last.getLastNameKana(),
                            first.getFirstNameRoman(), last.getLastNameRoman()
                    )
            );
        }

        return temporaryDummyUsers;
    }

    /**
     * フォームのメールアドレス情報からそれぞれのランダムなメールアドレスを作成しセットする
     * @param form フォームデータ
     * @param createSize 作成数
     */
    private void setMailAddress(CreateFromZeroForm form, int createSize){
        form.getMailAddressForms().forEach(mail -> {
            List<String> mailAddress = createRandomMailAddresses(
                    mail.getAccountLength(),
                    mail.getDomainName(),
                    createSize
            );
            mail.setMailAddresses(mailAddress);
        });
    }


    /**
     * ランダムにメールアドレスを生成し返却するメソッド
     * @param accountLength メールアドレスのアカウント名の長さ(注意：長さはロジックで1～5文字プラスされる)
     * @param domainName メールアドレスの＠以降の名前(指定が無い場合はxxx.xx.xxで初期化される)
     * @param createSize 作成するメールアドレスの数
     * @return ランダムに作成したメールアドレスのリスト
     */
    private List<String> createRandomMailAddresses(Integer accountLength, @Nullable String domainName, int createSize){
        if(!StringUtils.hasText(domainName)){
            domainName = "xxx.xx.xx";
        }

        if (accountLength == null || accountLength <= 0){
            accountLength = 12;
        }

        Random rand = new Random();

        List<String> mailAddresses = new ArrayList<>();
        for (int i = 0; i < createSize; i++) {
            String randStr = RandomString.make(accountLength +( rand.nextInt(5) + 1));
            StringBuilder sb = new StringBuilder();
            sb.append(randStr).append("@").append(domainName);
            mailAddresses.add(sb.toString());
        }

        return mailAddresses;
    }
}
