package com.bdb.bikedeboa.model.network;

import com.bdb.bikedeboa.model.network.response.LocalFull;
import com.bdb.bikedeboa.model.network.response.LocalLight;
import com.bdb.bikedeboa.model.network.response.ReviewResponse;
import com.bdb.bikedeboa.model.network.response.TagEntry;
import com.bdb.bikedeboa.model.network.response.Token;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Service {

	@FormUrlEncoded
	@POST("token")
	Call<Token> getAuthToken(@Field("username") String username,
							 @Field("password") String password);

	@GET("tag")
	Call<List<TagEntry>> getTagDitc(@Header("x-access-token") String authToken);

	@GET("local/light")
	Call<List<LocalLight>> getLocalsLight();

	@GET("local/{rackId}")
	Call<LocalFull> getLocalFull(@Path("rackId") int rackId,
								 @Header("x-access-token") String authToken);

	@FormUrlEncoded
	@POST("review")
	Call<ReviewResponse> postReview(@Field("idLocal") int rackId,
									@Field("rating") int nStars,
									@FieldMap Map<String, Integer> tagMap,
									@Header("x-access-token") String authToken);
}
