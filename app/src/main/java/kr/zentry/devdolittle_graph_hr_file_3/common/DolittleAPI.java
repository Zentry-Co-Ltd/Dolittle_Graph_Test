package kr.zentry.devdolittle_graph_hr_file_3.common;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class DolittleAPI {
    public interface DolittleAPIs {
        /*
                    animal category
                */
        @POST("api/v1/animal") // 동물 정보 등록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> animalRegister(@Header("authorization") String accessToken, @Body animalDataObj animalDataObj);

        @GET("api/v1/animal/{animalId}") // 동물 정보 가져오기 (Specific)
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> animalGetInfo(@Path(value = "animalId", encoded = true) String animalId, @Header("authorization") String accessToken);

        @POST("api/v1/animals") // 동물 정보 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> animalGetList(@Header("authorization") String accessToken);

        @GET("api/v1/animals/count") // 동물 등록 수 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> animalGetCount(@Header("authorization") String accessToken, @Header("x-refresh") String refreshToken);

        @DELETE("api/v1/animal") // 동물 정보 삭제
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> animalRemove(@Header("authorization") String accessToken, @Header("x-refresh") String refreshToken, @Header("animal") String animalId);

        @PUT("api/v1/animal") // 동물 정보 업데이트
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> animalUpdate(@Header("animal") String animalId, @Header("authorization") String accessToken, @Body animalDataObj animalDataObj);

        /*
            user category
        */
        @POST("api/v1/user") // 회원가입
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> userRegister(@Body userRegisterObj registerObj);

        @DELETE("api/v1/user") // 회원탈퇴
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> userExit(@Header("authorization") String accessToken);

        @GET("api/v1/user") // 유저 정보 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> userGet(@Header("authorization") String accessToken);

        /*
            session category
        */
        @POST("api/v1/session") // 로그인
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> sessionLogin(@Body sessionLoginObj loginObj);

        @DELETE("api/v1/session")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> sessionLogout(@Header("authorization") String accessToken);

        @GET("api/v1/session")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> sessionGet(@Header("authorization") String accessToken);

        /*
            manual category
        */
        @POST("api/v1/manual") // Manual 등록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> manualRegister(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body RateDataObj manualDataObj);

        @GET("api/v1/manual") // Manual 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> manualGetData(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("idx") int idx);

        @GET("api/v1/manual") // Manual 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> manualGetData(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/manuals") // Manual 여러개 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> manualGetDatas(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("type") String type);

        @GET("api/v1/manuals/count") // Manual 여러개 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> manualGetCount(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("type") String type);

        /*
            auto category
        */
        @POST("api/v1/auto") // auto 등록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> autoRegister(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body RateDataObj autoDataObj);

        @GET("api/v1/auto") // auto 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> autoGetData(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/autos") // auto 여러개 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> autoGetDatas(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("type") String type);

        @GET("api/v1/autos/count") // auto 여러개 가져오기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> autoGetCount(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("type") String type);

        /*
            sickness category
         */
        @POST("api/v1/sickness") // 병 기록 생성
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diseaseRegister(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body DiseaseDataObj diseaseDataObj);

        @GET("api/v1/sicknesses") // 모든 병 기록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diseaseGetDatas(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/sicknesses/count") // 모든 병 기록 갯수
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diseaseGetCount(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/sickness") // 선택한 병 기록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diseaseGetData(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body String _id);

        @DELETE("api/v1/sickness") // 병 기록 삭제
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diseaseDelete(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("_id") String _id);


        @PUT("api/v1/sickness") // 병 기록 수정
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diseaseUpdate(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body DiseaseUpdateDataObj diseaseUpdateDataObj);


        /*
            weight category
        */
        @POST("api/v1/weight") // 몸무게 생성
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> weightRegister(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body WeightDataObj weightDataObj);

        @GET("api/v1/weights") // 모든 몸무게 기록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> weightGetDatas(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/weight") // 선택한 몸무게 기록
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> weightGetData(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/weights/count") // 모든 몸무게 기록 갯수
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> weightsGetCount(@Header("authorization") String accessToken, @Header("animal") String animalId);

        /*
            diary category
         */
        @POST("api/v1/diary") // 일기 생성
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diaryRegister(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body DiaryDataObj diaryDataObj);

        @GET("api/v1/diaries") // 모든 일기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diaryGetDatas(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/diary") // 선택한 일기
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diaryGetData(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body String _id);

        @DELETE("api/v1/diary") // 일기 삭제
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diaryDelete(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("_id") String _id);

        @PUT("api/v1/diary") // 일기 업데이트
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diaryUpdate(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body DiaryUpdateObj diaryUpdateObj);

        @GET("api/v1/diaries/count") // 모든 일기 기록 갯수
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> diaryGetCount(@Header("authorization") String accessToken, @Header("animal") String animalId);

        /*
            terms category
        */

        @POST("api/v1/terms")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> TermTopic(@Header("authorization") String accessToken, @Header("conditions") String topic);

        @POST("api/v1/terms")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> setTermState(@Header("authorization") String accessToken, @Header("conditions") String topic, @Header("state") String state);

        @GET("api/v1/terms")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> getTerms(@Header("authorization") String accessToken);

        /*
            schedules category
        */
        @GET("api/v1/schedules/count")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> getScheduleCount(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/schedules")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> getScheduleList(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @POST("api/v1/schedule")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> createSchedule(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body scheduleCreateObj scheduleCreateObj);

        @PUT("api/v1/schedule")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> updateSchedule(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body scheduleUpdateObj scheduleUpdateObj);

        @DELETE("api/v1/schedule")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> deleteSchedule(@Header("authorization") String accessToken, @Header("animal") String animalId, @Header("_id") String alarmID);

        /*
            cardiac category
         */

        @POST("api/v1/cardiac")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> registerCardiac(@Header("authorization") String accessToken, @Header("animal") String animalId, @Body CardiacObj cardiacObj);

        @GET("api/v1/cardiac")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> getCardiac(@Header("authorization") String accessToken, @Header("animal") String animalId);

        @GET("api/v1/cardiacs")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> getCardiacs(@Header("authorization") String accessToken, @Header("animal") String animalId);

    }

    public interface KakaoAPIs {

        // https://dapi.kakao.com/v2/local/search/keyword.json?query=%EB%8F%99%EB%AC%BC%EB%B3%91%EC%9B%90&category_group_code=HP8&x=127.1079462&y=36.7977277&radius=5000&sort=distance&page=5
        @GET("v2/local/search/keyword.json")
        @Headers({"accept: */*",
                "content-type: application/json"})
        Call<ResponseBody> getList(@Header("Authorization") String key, @Query("query") String query, @Query("category_group_code") String category_group_code, @Query("x") String x, @Query("y") String y, @Query("radius") String radius, @Query("sort") String sort, @Query("page") String page);
    }

    public static class mapDocumentObj {
        private List<mapListObj> documents;

        public mapDocumentObj() {

        }

        public String getPlaceName(int i) {
            return documents.get(i).place_name;
        }

        public String getAddressName(int i) {
            return documents.get(i).address_name;
        }

        public String getDistance(int i) {
            return documents.get(i).distance;
        }

        public String getPlaceUrl(int i) {
            return documents.get(i).place_url;
        }

        public String getX(int i) {
            return documents.get(i).x;
        }

        public String getY(int i) {
            return documents.get(i).y;
        }

        public int Length() {
            return documents.size();
        }
    }

    public static class mapMetaObj {
        private mapMetaDataObj meta;

        public mapMetaObj() {

        }

        public mapMetaDataObj getMeta() {
            return meta;
        }

        public int getPageCount() {
            return meta.getPageable_count();
        }
    }

    public static class mapMetaDataObj {
        private boolean is_end;
        private int pageable_count;

        public mapMetaDataObj() {

        }

        public mapMetaDataObj(boolean is_end, int pageable_count) {
            this.is_end = is_end;
            this.pageable_count = pageable_count;
        }

        public Boolean getIs_end() {
            return is_end;
        }

        public int getPageable_count() {
            return pageable_count;
        }

        public void setIs_end(boolean is_end) {
            this.is_end = is_end;
        }

        public void setPageable_count(int pageable_count) {
            this.pageable_count = pageable_count;
        }
    }

    public static class mapListObj {
        private String address_name;
        private String category_group_code;
        private String category_group_name;
        private String category_name;
        private String distance;
        private String id;
        private String phone;
        private String place_name;
        private String place_url;
        private String road_address_name;
        private String x;
        private String y;

        public mapListObj() {

        }

        public mapListObj(String address_name, String category_group_code, String category_group_name, String category_name, String distance, String id, String phone, String place_name, String place_url, String road_address_name, String x, String y) {
            this.address_name = address_name;
            this.category_group_code = category_group_code;
            this.category_group_name = category_group_name;
            this.category_name = category_name;
            this.distance = distance;
            this.id = id;
            this.phone = phone;
            this.place_name = place_name;
            this.place_url = place_url;
            this.road_address_name = road_address_name;
            this.x = x;
            this.y = y;
        }

        public String getAddress_name() {
            return address_name;
        }

        public String getCategory_group_code() {
            return category_group_code;
        }

        public String getCategory_group_name() {
            return category_group_name;
        }

        public String getCategory_name() {
            return category_name;
        }

        public String getDistance() {
            return distance;
        }

        public String getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        public String getPlace_name() {
            return place_name;
        }

        public String getPlace_url() {
            return place_url;
        }

        public String getRoad_address_name() {
            return road_address_name;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setAddress_name(String address_name) {
            this.address_name = address_name;
        }

        public void setCategory_group_code(String category_group_code) {
            this.category_group_code = category_group_code;
        }

        public void setCategory_group_name(String category_group_name) {
            this.category_group_name = category_group_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }

        public void setPlace_url(String place_url) {
            this.place_url = place_url;
        }

        public void setRoad_address_name(String road_address_name) {
            this.road_address_name = road_address_name;
        }

        public void setX(String x) {
            this.x = x;
        }

        public void setY(String y) {
            this.y = y;
        }
    }

    public class sessionGetObj {
        private String _id;
        private String user;
        private Boolean valid;
        private String createdAt;
        private String updatedAt;
        private int __v;

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setValid(Boolean valid) {
            this.valid = valid;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String get_id() {
            return _id;
        }

        public int get__v() {
            return __v;
        }

        public Boolean getValid() {
            return valid;
        }

        public String getUser() {
            return user;
        }

    }

    public static class sessionLoginObj {
        private String email;
        private String password;

        public sessionLoginObj() {
        }

        public sessionLoginObj(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return this.email;
        }

        public String getPw() {
            return this.password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPw(String password) {
            this.password = password;
        }
    }

    public static class userRegisterObj {
        private String email;
        private String password;
        private String nickname;

        public userRegisterObj() {
        }

        public userRegisterObj(String email, String password, String nickname) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public String getNickname() {
            return nickname;
        }

        public String getPassword() {
            return password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class animalDataObj {
        private String name;
        private String birth;
        private Integer animal_type;
        private Integer gender;
        private String breed;
        private Integer cardiac;

        public animalDataObj() {
        }

        public animalDataObj(String name, String birth, Integer animal_type, Integer gender, String breed, Integer cardiac) {
            this.name = name;
            this.birth = birth;
            this.animal_type = animal_type;
            this.gender = gender;
            this.breed = breed;
            this.cardiac = cardiac;
        }

        public String getName() {
            return name;
        }

        public String getBirth() {
            return birth;
        }

        public Integer getAnimal_type() {
            return animal_type;
        }

        public Integer getGender() {
            return gender;
        }

        public String getBreed() {
            return breed;
        }

        public Integer getCardiac() {
            return cardiac;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setBirth(String birth) {
            this.birth = birth;
        }

        public void setAnimal_type(Integer animal_type) {
            this.animal_type = animal_type;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public void setBreed(String breed) {
            this.breed = breed;
        }

        public void setCardiac(Integer cardiac) {
            this.cardiac = cardiac;
        }
    }
    public static class RateDataObj {
        private String type;
        private Integer data;
        private String createdAt;

        public RateDataObj() {

        }

        public RateDataObj(String type, Integer data) {
            this.type = type;
            this.data = data;
        }

        public RateDataObj(String type, Integer data, String createdAt) {
            this.type = type;
            this.data = data;
            this.createdAt = createdAt;
        }

        public Integer getData() {
            return data;
        }

        public String getType() {
            return type;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setData(Integer data) {
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }


    public static class DiseaseDataObj {
        private String disease;

        public DiseaseDataObj() {
        }

        public DiseaseDataObj(String disease) {
            this.disease = disease;
        }

        public String getDisease() {
            return disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }
    }

    public static class DiseaseUpdateDataObj {
        private String _id;
        private DiseaseDataObj newData;

        public DiseaseUpdateDataObj() {
        }

        public DiseaseUpdateDataObj(String _id, DiseaseDataObj newData) {
            this._id = _id;
            this.newData = newData;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public DiseaseDataObj getNewData() {
            return newData;
        }

        public void setNewData(DiseaseDataObj newData) {
            this.newData = newData;
        }
    }

    public static class WeightDataObj {
        private float weight;
        private String unit;
        private Date date;

        public WeightDataObj() {

        }

        public WeightDataObj(float weight, String unit, Date date) {
            this.weight = weight;
            this.unit = unit;
            this.date = date;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static class DiaryDataObj {
        private String category;
        private String title;
        private String content;
        private Date date;

        public DiaryDataObj() {

        }

        public DiaryDataObj(String category, String title, String content, Date date) {
            this.category = category;
            this.title = title;
            this.content = content;
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class DiaryUpdateObj {
        private NewData newData;
        private String _id;

        public DiaryUpdateObj() {

        }

        public DiaryUpdateObj(String _id) {
            this._id = _id;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public NewData getNewData() {
            return newData;
        }

        public void setNewData(NewData newData) {
            this.newData = newData;
        }

        public static class NewData {
            private String category;
            private String title;
            private String content;

            public NewData() {
            }

            public NewData(String category, String title, String content) {
                this.category = category;
                this.title = title;
                this.content = content;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }

    public static class CardiacObj {
        private String data;

        public CardiacObj(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class scheduleCreateObj {
        private String title;
        private boolean alarmSwitch;
        private boolean repeat;
        private String time;
        private SheduleWeekObj week;

        public scheduleCreateObj() {

        }

        public scheduleCreateObj(String title, boolean alarmSwitch, boolean repeat, String time, SheduleWeekObj week) {
            this.title = title;
            this.alarmSwitch = alarmSwitch;
            this.repeat = repeat;
            this.time = time;
            this.week = week;
        }

        public String getTitle() {
            return title;
        }

        public SheduleWeekObj getWeek() {
            return week;
        }

        public String getTime() {
            return time;
        }

        public void setRepeat(boolean repeat) {
            this.repeat = repeat;
        }

        public void setWeek(SheduleWeekObj week) {
            this.week = week;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setAlarmSwitch(boolean alarmSwitch) {
            this.alarmSwitch = alarmSwitch;
        }

        public static class SheduleWeekObj {
            private boolean Monday;
            private boolean Tuesday;
            private boolean Wednesday;
            private boolean Thursday;
            private boolean Friday;
            private boolean Saturday;
            private boolean Sunday;

            public SheduleWeekObj() {

            }

            public SheduleWeekObj(boolean Monday, boolean Tuesday, boolean Wednesday, boolean Thursday, boolean Friday, boolean Saturday, boolean Sunday) {
                this.Monday = Monday;
                this.Tuesday = Tuesday;
                this.Wednesday = Wednesday;
                this.Thursday = Thursday;
                this.Friday = Friday;
                this.Saturday = Saturday;
                this.Sunday = Sunday;
            }

            interface setDay {
                void set(boolean isOn);
            }

            private setDay[] setDays = new setDay[]{
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setMonday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setTuesday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setWednesday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setThursday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setFriday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setSaturday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setSunday(isOn);
                        }
                    }
            };

            public SheduleWeekObj(boolean[] week) {
                for (int i = 0; i < week.length; i++) {
                    setDays[i].set(week[i]);
                }
            }

            public boolean getFriday() {
                return Friday;
            }

            public boolean getMonday() {
                return Monday;
            }

            public boolean getSaturday() {
                return Saturday;
            }

            public boolean getSunday() {
                return Sunday;
            }

            public boolean getThursday() {
                return Thursday;
            }

            public boolean getTuesday() {
                return Tuesday;
            }

            public boolean getWednesday() {
                return Wednesday;
            }

            public void setFriday(boolean friday) {
                Friday = friday;
            }

            public void setMonday(boolean monday) {
                Monday = monday;
            }

            public void setSaturday(boolean saturday) {
                Saturday = saturday;
            }

            public void setSunday(boolean sunday) {
                Sunday = sunday;
            }

            public void setThursday(boolean thursday) {
                Thursday = thursday;
            }

            public void setTuesday(boolean tuesday) {
                Tuesday = tuesday;
            }

            public void setWednesday(boolean wednesday) {
                Wednesday = wednesday;
            }

        }
    }

    public static class scheduleUpdateObj {
        private String _id;
        private String title;
        private boolean alarmSwitch;
        private boolean repeat;
        private String time;
        private SheduleWeekObj week;

        public scheduleUpdateObj() {

        }

        public scheduleUpdateObj(String _id, String title, boolean alarmSwitch, boolean repeat, String time, SheduleWeekObj week) {
            this.title = title;
            this.alarmSwitch = alarmSwitch;
            this.repeat = repeat;
            this.time = time;
            this.week = week;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public SheduleWeekObj getWeek() {
            return week;
        }

        public String getTime() {
            return time;
        }

        public void setRepeat(boolean repeat) {
            this.repeat = repeat;
        }

        public void setWeek(SheduleWeekObj week) {
            this.week = week;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setAlarmSwitch(boolean alarmSwitch) {
            this.alarmSwitch = alarmSwitch;
        }

        public static class SheduleWeekObj {
            private boolean Monday;
            private boolean Tuesday;
            private boolean Wednesday;
            private boolean Thursday;
            private boolean Friday;
            private boolean Saturday;
            private boolean Sunday;

            public SheduleWeekObj() {

            }

            public SheduleWeekObj(boolean Monday, boolean Tuesday, boolean Wednesday, boolean Thursday, boolean Friday, boolean Saturday, boolean Sunday) {
                this.Monday = Monday;
                this.Tuesday = Tuesday;
                this.Wednesday = Wednesday;
                this.Thursday = Thursday;
                this.Friday = Friday;
                this.Saturday = Saturday;
                this.Sunday = Sunday;
            }

            interface setDay {
                void set(boolean isOn);
            }

            private setDay[] setDays = new setDay[]{
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setMonday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setTuesday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setWednesday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setThursday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setFriday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setSaturday(isOn);
                        }
                    },
                    new setDay() {
                        @Override
                        public void set(boolean isOn) {
                            setSunday(isOn);
                        }
                    }
            };

            public SheduleWeekObj(boolean[] week) {
                for (int i = 0; i < week.length; i++) {
                    setDays[i].set(week[i]);
                }
            }

            public boolean getFriday() {
                return Friday;
            }

            public boolean getMonday() {
                return Monday;
            }

            public boolean getSaturday() {
                return Saturday;
            }

            public boolean getSunday() {
                return Sunday;
            }

            public boolean getThursday() {
                return Thursday;
            }

            public boolean getTuesday() {
                return Tuesday;
            }

            public boolean getWednesday() {
                return Wednesday;
            }

            public void setFriday(boolean friday) {
                Friday = friday;
            }

            public void setMonday(boolean monday) {
                Monday = monday;
            }

            public void setSaturday(boolean saturday) {
                Saturday = saturday;
            }

            public void setSunday(boolean sunday) {
                Sunday = sunday;
            }

            public void setThursday(boolean thursday) {
                Thursday = thursday;
            }

            public void setTuesday(boolean tuesday) {
                Tuesday = tuesday;
            }

            public void setWednesday(boolean wednesday) {
                Wednesday = wednesday;
            }

        }

    }
}
