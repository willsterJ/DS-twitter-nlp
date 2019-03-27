#!/bin/bash
#
##SBATCH --nodes=12
#SBATCH --nodes=12
#SBATCH --ntasks-per-node=4
#SBATCH --cpus-per-task=4
#SBATCH --time=48:00:00
#SBATCH --mem=20GB
#SBATCH --job-name=twitterRunTest
#SBATCH --mail-type=END
##SBATCH --mail-user=wj419@nyu.edu
#SBATCH --output=slurm_%j.out

module purge
module load maven/3.5.2

cd /scratch/wj419/twitter-nlp
mvn exec:java
