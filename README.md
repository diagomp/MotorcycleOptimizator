# MotorcycleOptimizator

### A Java program that aims to use Genetic Algorithms for the optimization of the rear suspension system of a racing motorcycle.

This project was born as a tool that aided Sevilla Racing engineers throghout the conceptual design phase of the development of their last racing motorcycle. It allows not only to evaluate, using grahps, the behavior of different design alternatives; but also automates the process of fine tuning all the parameters of the suspension system given a target behavior. Some of the features of this project are:

* Dynamic calculations of behavior graphs while modifying the design parameters.
* Posibility of testing with multiple rear suspension system types.
* Automatic optimization of the design parameters using Genetic Algorithms.
* Friendly graphical user interface without the need for any programming knowledge.


## How to use this project?
If you want to use this project, either because you are interested in the application itself or as a developer, the only requirement is to have JavaFX setted up in your PC.

1. Clone the repository on your PC.
2. Download JavaFX and import all the .jar files in the Build Path as External jars.
3. Add the following text to VM options:

``
--module-path /path/to/javafx-sdk-15.0.1/lib --add-modules javafx.controls,javafx.fxml
``

4. Open the file Main.java and compile it.   

## Known issues
This program was created for the Bachelor Thesis of a Degree in Mechanical Engineering. The scope was limited to developing a functional prototipe and testing whether it was adequate to use Genetic Algorithms in this context. For that reason, there is a lot of room for improvement - in one way to improve the lack of reliavility, but also to add new features that had been ignored due to deadlines. 

Nevertheless, the concept was proved to be realy interesting and to have a lot of potential. That's why I encourage engineers interested in the subject (may be students currently working for the Motostudent competition) to contribute to this project.

Here is a list of known issues and potential added features for you to work on:

*[] 
 
 ## License
 This project is [MIT Licensed](LICENSE).
