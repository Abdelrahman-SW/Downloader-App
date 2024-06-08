package com.yremhl.ystgdh.network;

import com.yremhl.ystgdh.Models.Admob;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {
    @GET("admob/readOne.php")
    Single<Admob> getAdmob(
    );
}
