workingDir=/home/data/users/ahoegger/downloads/scout
stagingArea=$workingDir/stagingArea
repositoriesDir=$workingDir
stageTriggerFileName=doStage



processZipFile()
{
  backupDir=$(pwd)
  zipFile=$backupDir"/"${1%?}
  sigOk=$2
  if [ $sigOk == OK ]; then
    echo $(date)" publish $zipFile"
    mkdir $stagingArea/working
    unzip $zipFile -d $stagingArea/working >$workingDir/NUL 
    cd $stagingArea/working
    for d in {[0-9\.]*,nightly}
    do
      if [ -d "$d" ]; then
        if  [ -d  $repositoriesDir/$d""_new ]; then
           rm -rf $repositoriesDir/$d""_new
        fi
        mv $stagingArea/working/$d $repositoriesDir/$d""_new
        # backup original
        if [ -d $repositoriesDir/$d ]; then
          if [ -d $repositoriesDir/$d""_backup ]; then
            rm -rf $repositoriesDir/$d""_backup
          fi
          mv $repositoriesDir/$d $repositoriesDir/$d""_backup
        fi
        mv $repositoriesDir/$d""_new $repositoriesDir/$d
      fi
    done
    cp -f $stagingArea/working/* $repositoriesDir/
    rm -rf $stagingArea/working
    cd $backupDir
  else
    echo "md5 not valid for $zipFile!"
  fi
}

if [ -f $stagingArea/$stageTriggerFileName ]; then
  backupDir=$(pwd)
  cd $stagingArea
  mv $stagingArea/$stageTriggerFileName $stagingArea/processing
  processZipFile $(md5sum -c $stagingArea/processing)
  mv $stagingArea/processing $stagingArea/$stageTriggerFileName 
  cd $backupDir
  rm $workingDir/NUL
fi

#echo $stagingArea/stage.zip
#username=$(ls -l $stagingArea/stage.zip | awk '{print $3}')
#if [ "$username" == "aho" ]; then

