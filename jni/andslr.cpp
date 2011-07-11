#include <jni.h>
#include <libraw.h>

extern "C" JNIEXPORT jbyteArray JNICALL Java_com_androsz_andslr_LibRaw_unpackThumbnailBytesToFit(JNIEnv * env, jobject obj, jstring jfilename, jshort height, jshort width)
{
	LibRaw raw;

	jboolean bIsCopy;
	const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);

	// Open the file and read the metadata
	raw.open_file(strFilename);

	(env)->ReleaseStringUTFChars(jfilename, strFilename);// release jstring

	// Let us unpack the image
	raw.unpack_thumb();

	jsize length = raw.imgdata.thumbnail.tlength;

	jbyteArray jb = (env)->NewByteArray(length);
	env->SetByteArrayRegion(jb,0,length,(jbyte *)raw.imgdata.thumbnail.thumb);

	// Finally, let us free the image processor for work with the next image
	raw.recycle();
	return jb;
}

extern "C" JNIEXPORT jshortArray JNICALL Java_com_androsz_andslr_LibRaw_getThumbnailDimensions(JNIEnv * env, jobject obj, jstring jfilename)
{
	LibRaw raw;

	jboolean bIsCopy;
	const char* strFilename = (env)->GetStringUTFChars(jfilename , &bIsCopy);

	// Open the file and read the metadata
	raw.open_file(strFilename);

	(env)->ReleaseStringUTFChars(jfilename, strFilename);// release jstring

	// Let us unpack the image
	raw.unpack_thumb();

	jshortArray js = (env)->NewShortArray(sizeof(jshort)*2);
	//env->SetShortArrayRegion(js,0,sizeof(jshort),(jshort*)raw.imgdata.thumbnail.theight);
	//env->SetShortArrayRegion(js,sizeof(jshort),sizeof(jshort)*2,(jshort*)raw.imgdata.thumbnail.twidth);

	raw.recycle();
	return js;
}
