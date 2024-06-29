### MFace

 ##### allows you to easily perform face detection and face matching.

Version: [![](https://jitpack.io/v/madenmustafa1/MFace.svg)](https://jitpack.io/#madenmustafa1/MFace)


## Include in project

###### Add it in your root build.gradle at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

###### Step 2. Add the dependency
	dependencies {
	        implementation 'com.github.madenmustafa1:MFace:Tag'
	}


### How do I use it?
###### Face Detection
	class MainActivity : AppCompatActivity(), MDetectorListener {
	
	      private val faceDetections = MFaceDetectorEntryPoint(_listener = this)
	 
	      override fun onCreate(savedInstanceState: Bundle?) {
	           ...
	           faceDetections.execute(bitmap)
	       }
	      
	      override fun onFaceDetected(face: Bitmap) {
	           
	      }
	
	      override fun onDetectorError(error: String) {
	
	      }
	
	      //Optional
	      override fun faceDetectorUIState(mFaceUiState: MFaceUIState)
	           super.faceDetectorUIState(MFaceUiState)
	      }
	}


###### Face Match
	class MainActivity : AppCompatActivity(), MDetectorListener, MFaceMatchLister {
	      override fun onCreate(savedInstanceState: Bundle?) {
	            //...
	            faceMatch = MFaceMatchEntryPoint(
	                 _context = this@MainActivity,
	                 _listener = this@MainActivity
	            )
	      }
	
	     override fun onFaceDetected(face: Bitmap) {
	          //Save Face 
	          faceMatch!!.addFace(name = viewModel.photoName, face = face)
	
	          //--- or ---
	  
	          //Recognize Face
	          faceMatch!!.recognizeFace(face)
	     }
	
	     override fun onRecognizeFace(result: Boolean, name: String) {
	 
	     }
	 
	     override fun onFaceMatchError(error: String) {
	 
	     }
	
	     //Optional
	     override fun addFaceResult(result: Boolean) {
	         
	     }
	  
	     //Optional
	     override fun faceMatchUIState(mFaceUiState: MFaceUIState) {
	 
	     }
	}



