# my-Android-dictionary-application
An Android dictionary application. I have used the [mdict-jni-query-library](https://github.com/Tuo-ZHANG/mdict-jni-query-library) I have written to access the dictionary of mdx format. 

## Build
Please use Android studio bumblebee. As this application uses JNI, NDK is also needed, make sure you have NDK installed in the SDK tools.

## Supporting features
- [x] quering multiple dictionaries at the same time
- [x] utilities for managing query histories with integration of SQLite 
- [x] download dictionary sources from the FastAPI backend
- [x] search through context menu
- [x] context menu search leads to Mdict app if installed, otherwise it leads to this app (query history is preserved all the same)
- [x] In context menu search, if the original token can not be found in any dictionaries, it would be lemmatized through FastAPI backend (currently supports German processing) before being fed into to the dictionary
- [x] In context menu search, if the input is text, the app would combine the first and last token of the text to form a new token, this is specifically for processing the Trennbare Verben in German
- [ ] view query history in order of alphabet, query frequencies or time 

## Showcase
- entries recycler view 

  ![alt text](https://github.com/Tuo-ZHANG/my-Android-dictionary-application/blob/master/c1dc866773adefd4ee841630678065f.jpg)
  
- utilities

  ![alt text](https://github.com/Tuo-ZHANG/my-Android-dictionary-application/blob/master/5cfe661e76c40689f7dba55d2fd7ccd.jpg)

- conjugation of German verbs

  ![alt text](https://github.com/Tuo-ZHANG/my-Android-dictionary-application/blob/master/Screenshot_1625774540.png)

- from conjugation to lemma 

  ![alt text](https://github.com/Tuo-ZHANG/my-Android-dictionary-application/blob/master/Screenshot_1625774451.png)
  
## Acknowledge
Many thanks to [terasum](https://github.com/terasum) for the consultation he provided. 

## Reference
https://bitbucket.org/xwang/mdict-analysis/src/master/

https://github.com/zhansliu/writemdict

https://github.com/terasum/js-mdict

https://github.com/dictlab/mdict-cpp

https://github.com/Tuo-ZHANG/mdict-jni-query-library
