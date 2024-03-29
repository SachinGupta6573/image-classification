import numpy as np
import tensorflow as tf 
from tensorflow import keras
from tensorflow.keras.models import Model,load_model
import socket
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.metrics import categorical_crossentropy
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications import imagenet_utils
from sklearn.metrics import confusion_matrix
import itertools 
import os,shutil,random,glob

ImageModel = load_model('ImageClassificationModel2.h5')

ImageModel.compile(optimizer=Adam(learning_rate=0.0001),loss='categorical_crossentropy',metrics=['accuracy'])
def prepare_image(file):
    img_path=''
    img = image.load_img(img_path+file,target_size=(224,224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array,axis=0)
    return tf.keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

def classify(path):
    preprocessed_image = prepare_image(path)
    predictions = ImageModel.predict(preprocessed_image)
    results = imagenet_utils.decode_predictions(predictions)
    print(results)
    print(results[0][0][-2])
    return results[0][0][-2]
server=socket.socket()
server.bind(('',8888))
server.listen(5)
c,add=server.accept()
print(add)
running=True
msg=c.recv(1024).decode()
while running:
    msg=c.recv(1024).decode()
    print(msg)
    if not msg:
        running=False
    if os.path.isfile(msg):
      try:
       result = classify(msg)+"`"
       print(result,bytes(result,'utf-8'))
       c.send(bytes(result,'utf-8'))
      except ValueError as e:
        print(msg,e)
    else:
        print('Some_error_occurred.`')
        c.send(b'Some_error_occurred.`')


