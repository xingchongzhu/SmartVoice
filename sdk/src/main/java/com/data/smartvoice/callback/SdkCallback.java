package com.data.smartvoice.callback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.data.smartvoice.entry.BaseResult;

import java.util.List;


public interface SdkCallback <T extends BaseResult>{
    /**
     * 服务器正常执行完成返回的结果
     * @param result
     */
    void onResponse(@NonNull T result);

    /**
     * 网络错误以及运行时异常
     * @param msg
     */
    void onFailure(@Nullable Object msg);

}
