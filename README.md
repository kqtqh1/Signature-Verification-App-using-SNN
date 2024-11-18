# Offline Signature Verification App using Siamese Neural Network
 
## Overview
This signature verification app uses a Siamese Neural Network to compare two handwritten signatures, with OpenCV handling the image preprocessing. The model was trained on 69,120 sample pairs, and the app was built in Android Studio. 

## Table of Contents
  - [Overview](#overview)
  - [Dataset](#dataset)
  - [Preprocessing Techniques](#preprocessing-techniques)
  - [Performance Evaluation](#performance-evaluation)
  - [Setting Up OpenCV](#setting-up-opencv-in-android-studio)
  - [Adding TF Lite in Android Studio](#adding-tensorflow-lite-model-in-android-studio)
  - [App](#app)
  - [License](#License)

## Dataset
The dataset consists of 69,120 handwritten signature sample pairs. These pairs include both genuine and forged signatures. The signatures in the dataset are extracted from two primary sources:
* [ICDAR 2011 Signature Dataset](https://www.kaggle.com/datasets/robinreni/signature-verification-dataset/data)
* Custom Dataset - Additional signatures collected specifically for this project to increase the diversity of the model.
* Data Labeling
  - Genuine Signatures (1): Both signatures are real.
  - Forged Signatures (0): One of the signatures in the pair is fake.

## Preprocessing Techniques
* Image Resizing
* Data Augmentation (Random rotation and brightness)
* Canny Edge Detection
* Normalization

## Performance Evaluation
The model was trained for over 100 epochs and tested on a 700-sample test dataset. Below are the results of the evaluation:
- Results on the Training Dataset

![Screenshot 2024-11-18 230707](https://github.com/user-attachments/assets/75433bc9-d4d1-41ef-9498-4bad1a9b6ca3)
![Screenshot 2024-11-18 230740](https://github.com/user-attachments/assets/b07b3b4f-b7ba-4ddb-be9f-f51c430c2a7d)

- Results on the Test Dataset
* Accuracy: 81.86%
* Precision: 81.41%
* Recall: 82.57%
* F1 Score: 81.99%

![12](https://github.com/user-attachments/assets/50c5f362-6e69-4aca-a98a-eb04ce8d9fe5)

## Setting Up OpenCV in Android Studio
1. [Install OpenCV SDK (OpenCV – 4.9.0)](https://opencv.org/releases/). Then extract it.
2. In Android Studio, go to **File > New > Import Module**. Navigate to the location where you extracted the OpenCV SDK, and select the sdk/java folder.
3. Open **build.gradle.kts(Module:app)** and add **'implementation(project(":openCV"))'** to the dependencies.

## Adding Tensorflow Lite model in Android Studio
1. Go to app/src/main and look for the asset folder. If it does not exist, create it.
2. Copy the **model.tflite** into the assets folder.
3. Open **build.gradle.kts(Module:app)** and add **'this.implementation ("org.tensorflow:tensorflow-lite:2.11.0")'** and **'this.implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.11.0")'** to the dependencies.
    
## App

![1cc14b2a-c480-45af-9edf-ff3bb52a41f5 (1)](https://github.com/user-attachments/assets/842394be-ba8b-4f97-96cd-5bc16bd8cfcd)  ![0ebb5911-4ba8-467e-87fc-d34a80c53975](https://github.com/user-attachments/assets/d7c795aa-2fd9-43ea-8ab9-04e3b839d618)

## License
This project is licensed under the MIT License. For more information, refer to the [LICENSE](LICENSE) file.


