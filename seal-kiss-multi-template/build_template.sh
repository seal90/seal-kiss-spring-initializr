#!/bin/bash

echo "template generate start"

current_temp_dir=$(pwd)

ignore_file=(".idea" ".multi_templates" "build_template.sh" "README_MODIFY.MD" "target" ".DS_Store" "." "..")

modify_file_suffix_reg=(".*\.java$" ".*\.MD$" ".*\.xml$" ".*\.yml")

if test -e "${current_temp_dir}"/.multi_templates
then
  rm -rf "${current_temp_dir}"/.multi_templates/*
else
  mkdir "${current_temp_dir}"/.multi_templates
fi

template_project_base_dir="${current_temp_dir}"/.multi_templates/{{MAVEN_ARTIFACT_ID}}
mkdir "${template_project_base_dir}"

array_contains() {
  for e in $1
  do
    if [[ $e == $2 ]]; then
      return 0
    fi
  done
  return 1
}

array_regex() {
  for e in $1
  do
    if [[ $2 =~ $e ]]; then
      return 0
    fi
  done
  return 1
}

cp_file() {
  origin_relative_path="$1"
  target_relative_path=`echo $origin_relative_path | sed 's@./seal-kiss-multi@./{{MAVEN_ARTIFACT_ID}}@'`
  target_relative_path=`echo $target_relative_path | sed 's@com/github/seal90/kiss/multi@{{PACKAGE_NAME_PATH}}@'`
  target_path="${template_project_base_dir}""/"$target_relative_path
  if [ ! -d $target_path ]
  then
    mkdir -p $target_path
  fi
  echo $target_path"/""$2"

  array_regex "${modify_file_suffix_reg[*]}" "$2"
  modify_file_flag=$?
  if test $modify_file_flag -eq 0; then
    sed  -e 's/com.github.seal90.kiss.multi/{{MAVEN_PACKAGE_NAME}}/g'\
     -e 's@<groupId>com.github.seal90</groupId>@<groupId>{{MAVEN_GROUP_ID}}</groupId>@g' \
     -e 's@<artifactId>seal-kiss-multi@<artifactId>{{MAVEN_ARTIFACT_ID}}@g'\
     -e 's@<name>seal-kiss-multi@<name>{{MAVEN_NAME}}@g' \
     -e 's@<description>Demo project for multi template@<description>{{MAVEN_DESCRIPTION}}@g' \
     -e 's@<module>seal-kiss-multi@<module>{{MAVEN_ARTIFACT_ID}}@g' \
     -e 's@\[seal-kiss-multi@\[{{MAVEN_ARTIFACT_ID}}@g' \
     -e 's@(seal-kiss-multi@({{MAVEN_ARTIFACT_ID}}@g' \
     -e 's@com%2Fgithub%2Fseal90%2Fkiss%2Fmulti@{{PACKAGE_NAME_URI_PATH}}@g' \
     -e 's@    name: seal-kiss-multi@    name: {{MAVEN_ARTIFACT_ID}}@g' \
     -e 's@  type-aliases-package: com.github.seal90.kiss.multi@  type-aliases-package: {{MAVEN_PACKAGE_NAME}}@g' \
     "$1""/""$2" > $target_path"/""$2"
  else
    cp "$1""/""$2" $target_path"/""$2"
  fi
}

recursion_dir_file() {
    for f in $(ls -a ${current_temp_dir}"/"$1)
    do
      array_contains "${ignore_file[*]}" "${f}"
      contains_flag=$?
      if test $contains_flag -ne 0; then
        if [ -f ${current_temp_dir}"/"$1"/"$f ]
        then
          cp_file "$1" "$f"
        else
          child_dir=$1"/"$f
          recursion_dir_file ${child_dir}
        fi
      fi
    done
}

recursion_dir_file .



echo "template generate end"