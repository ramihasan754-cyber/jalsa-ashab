<div align="center">

# 📱 تطبيق جلسة أصحاب

[![Download APK](https://img.shields.io/badge/تحميل_تطبيق_جلسة_أصحاب-APK_(v1.0.0)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/ramihasan754-cyber/jalsa-ashab/releases/download/v1.0.0/app-debug.apk)

[![Latest Release](https://img.shields.io/badge/الإصدار-v1.0.0-blue?style=flat-square)](https://github.com/ramihasan754-cyber/jalsa-ashab/releases/latest)

</div>

---
<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/2f39ebe0-fd37-48c4-b44f-c0c2901f0eca

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.
