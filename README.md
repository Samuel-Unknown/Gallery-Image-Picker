# Gallery-Image-Picker
Android library for picking images.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.samuel-unknown/gallery-image-picker/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.samuel-unknown/gallery-image-picker)

**Features:**
- You can use any image loading library for previews (*Glide*, *Picasso*, *Coil* etc.)
- Permission processing
- Returns name and Uri for picked images

<img src="/Gallery-Image-Picker.gif?raw=true" width="300px" align="middle">

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
    implementation 'io.github.samuel-unknown:gallery-image-picker:1.0.2'
}
```

## Usage
1. Create `ImageLoaderFactory` implementation  
    <details>
        <summary>Click to expand</summary>
   
    ```Kotlin
    // Example with Glide 
    class ImageLoaderFactoryGlideImpl(private val appContext: Context) : ImageLoaderFactory {

        val radius: Int by lazy {
            appContext.resources
                .getDimension(R.dimen.image_corner_radius)
                .roundToInt()
        }

        override fun create(): ImageLoader = object : ImageLoader {
            override fun load(imageView: ImageView, uri: Uri) {

                Glide.with(appContext)
                    .load(uri)
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(radius)
                        )
                    )
                    .placeholder(R.drawable.bg_placeholder)
                    .into(imageView)
            }

            override fun cancel(imageView: ImageView) {
                Glide.with(appContext).clear(imageView)
            }
        }
    }
    ```
    </details>

2. Initialize library with `ImageLoaderFactory` implementation
    <details>
        <summary>Click to expand</summary>
    
    ```Kotlin
    class Application: Application() {
        override fun onCreate() {
            super.onCreate()
            initGalleryImagePickerLib()
        }

        private fun initGalleryImagePickerLib() {
            GalleryImagePicker.init(ImageLoaderFactoryGlideImpl(appContext = this))
        }
    }
    ```
    </details>

3. Register launcher and launch it when it needed
    <details>
        <summary>Click to expand</summary>
    
    ```Kotlin
    class MainActivity : AppCompatActivity() {

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

            openGalleryButtonView.setOnClickListener {
                getImagesLauncher.launch(GalleryConfigurationDto())
            }
        }
    }
    ```
    </details>

## Development roadmap
#### Version 1.1.*
- [x] Mime types support with config
- [x] Handle screen orientation changes
- [ ] UI customizations *(in progress)*
#### Version 1.2.*
- [ ] Directory choosing 
- [ ] Perfomance improvements
#### Version 1.3.*
- [ ] Camera integration (preview and capture)

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
