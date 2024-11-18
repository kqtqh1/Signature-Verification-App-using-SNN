import tensorflow as tf

def manhattan_distance(embeddings): 
    import tensorflow as tf
    embedding1, embedding2 = embeddings
    return tf.abs(embedding1 - embedding2)

model = tf.keras.models.load_model("\\Users\\kathl\\Downloads\\unique.keras", custom_objects={'manhattan_distance': manhattan_distance})

converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.target_spec.supported_ops = [
    tf.lite.OpsSet.TFLITE_BUILTINS,   # use TensorFlow Lite built-in operations
    tf.lite.OpsSet.SELECT_TF_OPS  #allow selected TensorFlow operations for compatibility
]

#convert the Keras model to tflite file format
tflite_model = converter.convert()

#save the converted model
with open("modeling.tflite", "wb") as f:
    f.write(tflite_model)
