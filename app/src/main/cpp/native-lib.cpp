#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_sneyder_sdmessages_data_hashGenerator_AppHasher_getPepper(
        JNIEnv *env,
        jobject /* this */) {
    std::string pepper = "timafaptiwuftfap";
    return env->NewStringUTF(pepper.c_str());
}
