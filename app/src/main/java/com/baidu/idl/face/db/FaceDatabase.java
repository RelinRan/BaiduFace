package com.baidu.idl.face.db;

import android.util.Base64;
import android.util.Log;

import com.baidu.idl.face.face.FaceSDK;
import com.baidu.idl.face.sqlite.SQLite;
import com.baidu.idl.face.utils.Hex;

import java.util.List;

/**
 * 人脸数据库
 */
public class FaceDatabase {

    private String TAG = FaceDatabase.class.getSimpleName();
    private SQLite sqLite;
    private FaceSDK faceSDK;


    public FaceDatabase() {
        faceSDK = FaceSDK.getInstance();
        sqLite = SQLite.administrator();
    }

    /**
     * 添加用户
     *
     * @param faceID
     * @param feature
     * @param userId
     * @param userName
     * @param avatar
     * @return
     */
    public long addUser(int faceID, byte[] feature, String userId, String userName, byte[] avatar) {
        FaceTable table = new FaceTable();
        table.setFaceID(faceID);
        table.setFeature(Hex.toHex(feature));
        table.setUserId(userId);
        table.setUserName(userName);
        table.setAvatar(Base64.encodeToString(avatar, Base64.DEFAULT));
        table.setFaceTime(System.currentTimeMillis());
        //查询是否已存在，更新数据
        FaceTable userFace = queryByUserId(userId);
        if (userFace != null) {
            table.setFaceID(userFace.getFaceID());
            int updateIndex = updateUser(table);
            Log.i(TAG, "addUser user face exist to update index:" + updateIndex);
            return updateIndex;
        }
        //查询新数据
        int pushPersonByIdIndex = faceSDK.pushPersonById(faceID, feature);
        long index = sqLite.insert(table);
        Log.i(TAG, "addUser pushPersonByIdIndex:" + pushPersonByIdIndex + ",index:" + index);
        return index;
    }

    /**
     * 通过用户id删除
     *
     * @param userId
     * @return
     */
    public int deleteByUserId(String userId) {
        FaceTable table = queryByUserId(userId);
        int delPersonByIdIndex = 0;
        if (table != null) {
            delPersonByIdIndex = faceSDK.delPersonById(table.getFaceID());
        }
        int index = sqLite.delete(FaceTable.class, "userId = ?", new String[]{userId});
        Log.i(TAG, "deleteByUserId delPersonByIdIndex:" + delPersonByIdIndex + ",index:" + index);
        return index;
    }

    /**
     * 更新用户信息
     *
     * @param table
     */
    public int updateUser(FaceTable table) {
        int index = sqLite.update(table, "userId = ?", new String[]{table.getUserId() + ""});
        Log.i(TAG, "updateUser index:" + index);
        return index;
    }

    /**
     * 查询所有人脸
     *
     * @return
     */
    public List<FaceTable> queryAll() {
        return sqLite.query(FaceTable.class);
    }

    /**
     * 根据人脸ID查询用户
     *
     * @param faceID
     * @return
     */
    public FaceTable queryByFaceID(int faceID) {
        List<FaceTable> list = sqLite.query(FaceTable.class, "select * from " + FaceTable.class.getSimpleName() + " where faceID = " + faceID);
        int size = list == null ? 0 : list.size();
        if (size > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据用户ID查询用户
     *
     * @param userId
     * @return
     */
    public FaceTable queryByUserId(String userId) {
        List<FaceTable> list = sqLite.query(FaceTable.class, "select * from " + FaceTable.class.getSimpleName() + " where userId = " + userId);
        int size = list == null ? 0 : list.size();
        if (size > 0) {
            return list.get(0);
        }
        return null;
    }

}
