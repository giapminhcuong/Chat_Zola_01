package com.framgia.arutalk.model;

/**
 * Created by Admin on 22/5/2017.
 */

public class Constant {
    public final static String PACKAGE_NAME = "com.framgia.arutalk";
    public final static String REGEX_SPLIT_ERROR_SIGN_IN = ":";

    public class Storage {
        public final static String AVATARS = "avatars";
    }

    public class Database {
        public final static String USERS = "users";

        public class User {
            public final static String URI_PHOTO = "uriPhoto";
        }
    }

    public class BroadcastIntent {
        public class UploadAvatar {
            public final static String ACTION_UPLOAD_AVATAR_SUCCESSFUL = PACKAGE_NAME + "" +
                ".ACTION_UPLOAD_AVATAR_SUCCESSFUL";

            public final static String EXTRA_UPLOAD_AVATAR_SUCCESSFUL =
                ".EXTRA_UPLOAD_AVATAR_SUCCESSFUL";
        }
    }
}
