package com.asoft.timemarks.rest;

import com.asoft.timemarks.models.ResCheckJoinStatus;
import com.asoft.timemarks.models.ResSendOtp;
import com.asoft.timemarks.models.ResUploadImage;
import com.asoft.timemarks.models.Result;
import com.asoft.timemarks.models.response.ResCheckTransaction;
import com.asoft.timemarks.models.response.ResGetBalance;
import com.asoft.timemarks.models.response.ResGetComparision;
import com.asoft.timemarks.models.response.ResGetHash;
import com.asoft.timemarks.models.response.ResGetHomeData;
import com.asoft.timemarks.models.response.ResGetLeaderboard;
import com.asoft.timemarks.models.response.ResGetQuizAnswers;
import com.asoft.timemarks.models.response.ResGetQuizChapters;
import com.asoft.timemarks.models.response.ResGetQuizFeed;
import com.asoft.timemarks.models.response.ResGetQuizQuestions;
import com.asoft.timemarks.models.response.ResGetQuizes;
import com.asoft.timemarks.models.response.ResGetSingleQuiz;
import com.asoft.timemarks.models.response.ResGetSubjects;
import com.asoft.timemarks.models.response.ResGetTransactions;
import com.asoft.timemarks.models.response.ResSubmitQuiz;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("webservices/login.php?")
    Call<Result> userLogin(
            @Field("uname") String uname,
            @Field("upass") String upass,
            @Field("login_type") String login_type
    );
    @FormUrlEncoded
    @POST("webservices/add_comment.php?")
    Call<Result> addComment(
            @Field("comment") String comment,
            @Field("event_id") String event_id,
            @Field("profileImage") String profileImage,
            @Field("firstName") String firstName
    );
    @FormUrlEncoded
    @POST("webservices/forgot_password.php?")
    Call<Result> forgotPassword(
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("webservices/signup.php?")
    Call<Result> userSignUp(
            @Field("uname") String uname,
            @Field("upass") String upass,
            @Field("cupass") String cupass,
            @Field("uemail") String uemail,
            @Field("phone") String phone,
            @Field("image") String image,
            @Field("referral_id") String referral_id
    );

    @FormUrlEncoded
    @POST("webservices/social_signup.php?")
    Call<Result> userSignUpWithSocial(
            @Field("name") String first_name,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("image") String image,
            @Field("login_type") String login_type,
            @Field("referral_id") String referral_id
    );

    @FormUrlEncoded
    @POST("webservices/update_profile.php?")
    Call<Result> updateProfile(
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("address1") String address1,
            @Field("address2") String address2,
            @Field("city") String city,
            @Field("state") String state,
            @Field("zip") String zip,
            @Field("country") String country,
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("email") String email,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("webservices/update_password.php?")
    Call<Result> updatePassword(
            @Field("user_id") String user_id,
            @Field("current_password") String current_password,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("webservices/changePassword.php?")
    Call<Result> updatePasswordWithOtp(
            @Field("mobile") String mobile,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("webservices/forgotPassword.php?")
    Call<ResSendOtp> sendOtp(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("webservices/forgotPassword.php?")
    Call<ResSendOtp> reSendOtp(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("webservices/signup_resend_otp.php?")
    Call<ResSendOtp> reSendOtpRegister(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("webservices/otpVerfication.php?")
    Call<ResSendOtp> checkOtp(@Field("mobile") String mobile, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("webservices/signupOtpVerfication.php?")
    Call<ResSendOtp> checkOtpRegister(@Field("mobile") String mobile, @Field("otp") String otp);

    /*@Multipart
    @POST("webservices/imageUpload.php?")
    @Headers({
            "Accept: application/json"
    })
    Call<Result> uploadImage(@Part MultipartBody.Part image, @Part("user_id") RequestBody user_id);*/


    @Multipart
    @POST("webservices/imageUpload.php?")
    @Headers({
            "Accept: application/json"
    })
    Call<ResUploadImage> uploadImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("webservices/fileUpload.php?")
    @Headers({
            "Accept: application/json"
    })
    Call<ResUploadImage> uploadProfileImage(@Part MultipartBody.Part image, @Part("user_id") RequestBody user_id, @Part("display_name") RequestBody display_name);

    @FormUrlEncoded
    @POST("webservices/quizQuestions.php?")
    Call<ResGetQuizQuestions> getQuizQuestions(
            @Field("chapter_id") String chapter_id,
            @Field("difficulty_level") String difficulty_level
    );

    @FormUrlEncoded
    @POST("webservices/single_answer_report.php?")
    Call<ResGetQuizAnswers> getQuizAnswers(
            @Field("chapter_id") String chapter_id,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("quiz/webservices/chapterList.php?")
    Call<ResGetQuizChapters> getQuizChapters(
            @Field("subject_id") String subject_id
    );

    @FormUrlEncoded
    @POST("webservices/chapterList.php?")
    Call<ResGetQuizes> getQuizes(
            @Field("user_id") String user_id,
            @Field("subject_id") String subject_id,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("webservices/user_quiz_list.php?")
    Call<ResGetQuizes> getMyQuizes(
            @Field("user_id") String user_id,
            @Field("subject_id") String subject_id
    );

    @FormUrlEncoded
    @POST("webservices/add_answerRecord.php?")
    Call<ResSubmitQuiz> submitQuiz(
            @Field("user_id") String user_id,
            @Field("response_dec") String response_dec,
            @Field("attempt_time") String attempt_time,
            @Field("chapter") String chapter,
            @Field("level") String level
    );

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET("webservices/subjectlist.php?")
    Call<ResGetSubjects> getQuizSubjects();

    @FormUrlEncoded
    @POST("quiz/webservices/feed.php?")
    Call<ResGetQuizFeed> getQuizFeed(
            @Field("user_id") String user_id,
            @Field("level") String level
    );

    @FormUrlEncoded
    @POST("quiz/webservices/user_feed.php?")
    Call<ResGetQuizFeed> getUserFeed(
            @Field("user_id") String user_id,
            @Field("log_user_id") String log_user_id,
            @Field("last_id") String last_id
    );

    @FormUrlEncoded
    @POST("quiz/webservices/leaderboard.php")
    Call<ResGetLeaderboard> getLeaderboard(
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("quiz/webservices/compare_feed.php")
    Call<ResGetComparision> getComparision(
            @Field("user_id") String user_id,
            @Field("comapre_user_id") String comapre_user_id,
            @Field("subject_id") String subject_id,
            @Field("chapter_id") String chapter_id,
            @Field("level") String level,
            @Field("unique_exam_code") String unique_exam_code
    );

    @FormUrlEncoded
    @POST("quiz/webservices/like.php")
    Call<Result> likePost(
            @Field("user_id") String user_id,
            @Field("unique_exam_code") String unique_exam_code
    );

    @FormUrlEncoded
    @POST("webservices/PayuMoneyHashCalculation.php?")
    Call<ResGetHash> calculateHash(
            @Field("user_id") String user_id,
            @Field("KEY") String KEY,
            @Field("TXNID") String TXNID,
            @Field("AMOUNT") String AMOUNT,
            @Field("PRODUCT_INFO") String PRODUCT_INFO,
            @Field("FIRSTNAME") String FIRSTNAME,
            @Field("EMAIL") String EMAIL,
            @Field("UDF1") String UDF1,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("webservices/payumoney_checksum_for_joining.php?")
    Call<ResGetHash> calculateHashForJoining(
            @Field("user_id") String user_id,
            @Field("quiz_id") String quiz_id,
            @Field("KEY") String KEY,
            @Field("TXNID") String TXNID,
            @Field("AMOUNT") String AMOUNT,
            @Field("PRODUCT_INFO") String PRODUCT_INFO,
            @Field("FIRSTNAME") String FIRSTNAME,
            @Field("EMAIL") String EMAIL,
            @Field("UDF1") String UDF1,
            @Field("UDF2") String UDF2,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("webservices/check_transaction")
    Call<ResCheckTransaction> checkTransaction(
            @Field("user_id") String user_id,
            @Field("txnId") String txnId,
            @Field("paymentId") String paymentId,
            @Field("amount") String amount
    );

    @FormUrlEncoded
    @POST("webservices/checkBalance.php")
    Call<ResGetBalance> checkBalance(
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("webservices/check_joining_status.php")
    Call<ResCheckJoinStatus> checkJoiningStatus(
            @Field("user_id") String user_id,
            @Field("quiz_id") String quiz_id
    );

    @FormUrlEncoded
    @POST("webservices/joining_by_wallet.php")
    Call<Result> joinContestByWallet(
            @Field("user_id") String user_id,
            @Field("quiz_id") String quiz_id
    );

    @FormUrlEncoded
    @POST("webservices/slider.php")
    Call<ResGetHomeData> getHomeData(
            @Field("user_id") String user_id,
            @Field("subject_id") String subject_id,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("webservices/transactions.php")
    Call<ResGetTransactions> getTransactions(
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("webservices/single_quiz.php")
    Call<ResGetSingleQuiz> getSingleQuiz(
            @Field("user_id") String user_id,
            @Field("quiz_id") String quiz_id
    );

    @FormUrlEncoded
    @POST("webservices/chapterWiseUserRank.php")
    Call<ResGetLeaderboard> getQuizLeaderboard(
            @Field("user_id") String user_id,
            @Field("quiz_id") String quiz_id
    );
}
