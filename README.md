Twitter NLP Project analyzes a .csv file of tweets and will output the top 20 @ handle and # mentions
on .txt files. It will also perform and use Stanford's CoreNLP and apply a Ngram algorithm to specify parameters to be included in the top list such as Part-of-Speech, dependency parser, and named entity. 
This project is meant to be run on a cluster. The cluster that is used (and that will be referenced)
in the instructions is NYU's HPC Cluster. 

Instructions on Windows:
- unzip the file
- download Putty and WinSCP. Follow the instructions on how to setup here: 
    - https://wikis.nyu.edu/pages/viewpage.action?pageId=84612833
    - https://wikis.nyu.edu/display/NYUHPC/Accessing+HPC+clusters+from+Windows#AccessingHPCclustersfromWindows-TUNNEL
- download a unix bash console. The one that I use is MINGW64
- open Putty. Load the configuration as specified by the tutorial
- open WinSCP. Enter the following:
    - Hostname: localhost
    - select file protocol: SFTP
    - port number: 8026
    - enter your username and password, login
- On the right panel, direct yourself to /scratch/<your username>
- On the left panel, direct yourself to where this project folder is located
- The main class is found in src/main/java/Default.java
- Do take a look at it and comment/uncomment the tasks you would like to perform
- drag project folder to right panel
- Now open the unix bash console
- ssh <username>@prince.hpc.nyu.edu
- cd yourself to /scratch/<your username>s
- type: sbatch sbatchConfig.s !!!!!!!! Not working, see below
- output file will a "slurm-xxxx.out" file

Running sbatch will result in compilation error. However, running it on an interactive shell works, albeit requiring you to be connected to the server while program is executing.
To access the interactive shell,
- srun --mem=10GB --time=04:00:00 --nodes=12 --cpus-per-task 4 --pty $SHELL
- module load maven/3.5.2
- mvn clean install
- mvn exec:java