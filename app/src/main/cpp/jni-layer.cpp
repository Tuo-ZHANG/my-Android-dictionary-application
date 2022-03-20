#include <jni.h>
#include <string>
#include <cstring>
#include <android/log.h>

#include "mdict_extern.h"
#include <cstdlib>

std::string query(int, char **);

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_entryPoint(JNIEnv *env, jobject thiz, jstring input1,
                                                       jstring input2) {
    // TODO
    const int argc = 3;
    const char *placeholder = "placeholder";
    char *argv[argc];
    const char *path = env->GetStringUTFChars(input1, nullptr);
    const char *word = env->GetStringUTFChars(input2, nullptr);

    argv[0] = strdup(placeholder);
    argv[1] = strdup(path);
    argv[2] = strdup(word);

    std::string definition = query(argc, argv);

//    void* dict = mdict_init(argv[1], "en_US.aff", "en_US.dic");
//    char* result[0];
//    mdict_lookup(dict, argv[2], result);
//    std::string definition(*result);
//    __android_log_print(ANDROID_LOG_INFO, "length", "%zu \n", strlen(*result));
//    mdict_destory(dict);
//    if (*result != nullptr) {
//        free(*result);
//    }

    env->ReleaseStringUTFChars(input1, path);
    env->ReleaseStringUTFChars(input2, word);

    free(argv[0]);
    free(argv[1]);
    free(argv[2]);
    __android_log_print(ANDROID_LOG_INFO, "length", "%zu \n", strlen(definition.c_str()));
    return env->NewStringUTF(definition.c_str());;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_ContextMenuInitiatedActivity_entryPoint(JNIEnv *env, jobject thiz,
                                                                       jstring input1,
                                                                       jstring input2) {
    const int argc = 3;
    const char *placeholder = "placeholder";
    char *argv[argc];
    const char *path = env->GetStringUTFChars(input1, nullptr);
    const char *word = env->GetStringUTFChars(input2, nullptr);

    argv[0] = strdup(placeholder);
    argv[1] = strdup(path);
    argv[2] = strdup(word);

    std::string definition = query(argc, argv);

    env->ReleaseStringUTFChars(input1, path);
    env->ReleaseStringUTFChars(input2, word);

    free(argv[0]);
    free(argv[1]);
    free(argv[2]);
    __android_log_print(ANDROID_LOG_INFO, "length", "%zu \n", strlen(definition.c_str()));
    return env->NewStringUTF(definition.c_str());;
}