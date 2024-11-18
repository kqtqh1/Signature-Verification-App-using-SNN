﻿# Offline Signature Verification App using Siamese Neural Network
 
## Overview
This signature verification app uses a Siamese Neural Network to compare two signatures, with OpenCV handling the image preprocessing. The model was trained on 69,120 sample pairs, and the app was built in Android Studio. 

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
* Data Augmentation
* Canny Edge Detection
* Normalization

## Performance Evaluation
The model is trained with over 100 epochs and tested on a 700-sample test dataset. Below are the results of the evaluation:

* Accuracy: 81.71%
* Precision: 81.18%
* Recall: 82.57%
* F1 Score: 81.87%

## Setting Up OpenCV in Android Studio
1. [Install OpenCV SDK (OpenCV – 4.9.0)](https://opencv.org/releases/). Then extract it.
2. In Android Studio, go to **File > New > Import Module**. Navigate to the location where you extracted the OpenCV SDK, and select the sdk/java folder.
3. Open **build.gradle.kts(Module:app)** and add **implementation(project(":openCV"))** to the dependencies.

## Adding Tensorflow Lite model in Android Studio
1. Go to app/src/main and look for the asset folder. If it does not exist, create it.
2. Copy the **model.tflite** into the assets folder.
3. Open **build.gradle.kts(Module:app)** and add **this.implementation ("org.tensorflow:tensorflow-lite:2.11.0")** and **this.implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.11.0")** to the dependencies.
    
## App

![1cc14b2a-c480-45af-9edf-ff3bb52a41f5 (1)](https://github.com/user-attachments/assets/d08565b8-5e8b-4ff9-add3-d5b2a626343a)
![0ebb5911-4ba8-467e-87fc-d34a80c53975](https://github.com/user-attachments/assets/649e0a1d-fe86-48e6-97d0-0ef96f116bff)

## License



