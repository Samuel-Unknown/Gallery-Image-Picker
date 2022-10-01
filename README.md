# Gallery-Image-Picker
Android library for picking images.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.samuel-unknown/gallery-image-picker/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.samuel-unknown/gallery-image-picker)

## Features
- You can use any image loading library for previews (*Glide*, *Picasso*, *Coil* etc.)
- Permission processing
- Returns name and Uri for picked images
- UI customizations
- Filtering by mime types and folders

<p align="center">
<img src="/Gallery-Image-Picker.gif?raw=true" width="300px" align="middle">
</p>

## Add library to a project
Add the mavenCentral repository in root build.gradle:
```
allprojects {
    repositories {
        mavenCentral()
    }
}
```
Add the following dependency in app build.gradle:
```
dependencies {
    implementation 'io.github.samuel-unknown:gallery-image-picker:1.5.0'
    
    // if you want to use default Glide implementation for ImageLoaderFactory
    implementation 'io.github.samuel-unknown:gallery-image-picker-glide:1.5.0'
    
    // if you want to use default Coil implementation for ImageLoaderFactory
    implementation 'io.github.samuel-unknown:gallery-image-picker-coil:1.5.0'
}
```

## Usage
1. Create custom `ImageLoaderFactory` implementation or use default implementation (Glide or Coil) by adding dependency as said above. Glide implementation works faster.

2. Initialize library with `ImageLoaderFactory` implementation
    <details>
        <summary>Click to expand</summary>
    
    ```Kotlin
    // MyApplication.kt
    class MyApplication: Application(R.layout.activity_main) {
        override fun onCreate() {
            super.onCreate()
            initGalleryImagePickerLib()
        }

        private fun initGalleryImagePickerLib() {
            GalleryImagePicker.init(ImageLoaderFactoryGlideImpl(appContext = this))
        }
    }
    ```
    
    ```Xml
    <!--AndroidManifest.xml-->
    <application
        android:name=".MyApplication"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true">
    </application>
    ```
    </details>

3. Register launcher and launch it when it needed
    <details>
        <summary>Click to expand</summary>
    
    ```Kotlin
    // MyApplication.kt
    class MainActivity : AppCompatActivity(R.layout.activity_main) {
        private val getImagesLauncher = registerForActivityResult(ImagesResultContract()) { result: ImagesResultDto ->
            when (result) {
                is ImagesResultDto.Success -> {
                    result.images.forEach { imageDto ->
                        Log.d(TAG, "imageDto: $imageDto")
                     }
                }
                is ImagesResultDto.Error -> {
                    Log.d(TAG, "error: ${result.message}")
                }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            findViewById<Button>(R.id.open_gallery_button_view).setOnClickListener {
                getImagesLauncher.launch(
                    GalleryConfigurationDto(
                        spacingSizeInPixels = 30,
                        spanCount = 4,
                        openLikeBottomSheet = true,
                        singleSelection = false,
                        peekHeightInPercents = 60
                    )
                )
            }
        }
    
        companion object {
            private val TAG = MainActivity::class.java.simpleName
        }
    }
    ```
    </details>
4. Customizations
    <details>
        <summary>Click to expand</summary>
    
    `class GalleryConfigurationDto` uses for customizations. There are different arguments for that:
    - `@Px val spacingSizeInPixels: Int` - for spacing between cells.
    - `val spanCount: Int` - for setting cells count.
    - `val openLikeBottomSheet: Boolean` - should be gallery opened like BottomSheet or in full-screen mode.
    - `val singleSelection: Boolean` - limits selection to one image,
    - `val peekHeightInPercents: Int` - for setting BottomSheet height.
    - `val mimeTypes: List<String>? = null` - filtering images by mimeTypes.
    - `@StyleRes val themeResId: Int` - for creating a custom theme.
    
        You can find an example of using here [here](https://github.com/Samuel-Unknown/Gallery-Image-Picker/tree/master/sample)
    </details>
## Development roadmap
#### Version 1.1.*
- [x] Mime types support with config
- [x] Handle screen orientation changes
- [x] UI customizations
#### Version 1.2.*
- [x] Add Glide and Coil implementation modules
#### Version 1.3.*
- [x] Directory choosing
#### Version 1.4.*
- [x] Single image selection
#### Version 1.5.*
- [x] Performance improvements
#### Version 1.6.*
- [ ] Camera integration (preview and capture) *{in progress}*

## License
```
Copyright 2021 Samuel Unknown

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
