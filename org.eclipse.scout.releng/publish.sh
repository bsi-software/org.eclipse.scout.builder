workingDir=/home/aho/eclipseUpload
stagingArea=$workingDir/stagingArea
repositoriesDir=$workingDir/repository
stageTriggerFileName=doStage



processZipFile()
{
  backupDir=$(pwd)
  zipFile=$backupDir"/"${1%?}
  echo	"zip file name '$zipFile'"
  sigOk=$2
  if [ $sigOk == OK ]; then
    echo "process zip file $zipFile"
    mkdir $stagingArea/working
    unzip $zipFile -d $stagingArea/working
    cd $stagingArea/working
    for d in {[0-9\.]*,nightly}
    do
      if [ -d "$d" ]; then
        # backup original
        if [ -d $repositoriesDir/$d ]; then
          echo "backup repository $d"
          if [ -d $repositoriesDir/$d""_backup ]; then
            rm -rf $repositoriesDir/$d""_backup
          fi
          mkdir $repositoriesDir/$d""_backup
          mv -f $repositoriesDir/$d $repositoriesDir/$d""_backup
        fi
      fi
    done
    cp -rf $stagingArea/working/* $repositoriesDir/
    cd $backupDir
  fi
}

echo "start"
if [ -f $stagingArea/$stageTriggerFileName ]; then
  backupDir=$(pwd)
  cd $stagingArea
  mv $stagingArea/$stageTriggerFileName $stagingArea/processing
  processZipFile $(md5sum -c $stagingArea/processing)
#  for f in $stagingArea/stage_[0-9]*\.zip
#  do
#    processStageZip $f
#  done
#  rm $stagingArea/processing
  mv $stagingArea/processing $stagingArea/$stageTriggerFileName 
  cd $backupDir
fi

#echo $stagingArea/stage.zip
#username=$(ls -l $stagingArea/stage.zip | awk '{print $3}')
#if [ "$username" == "aho" ]; then

