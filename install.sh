#!/bin/bash

set -e

if [ -z "$MARATHON_DIR" ]; then
  MARATHON_DIR="$HOME/.marathon"
fi

install_marathon_from_git() {
  if [ -z "$MARATHON_SOURCE" ]; then
    MARATHON_SOURCE="https://github.com/anallely/marathon-runner.git"
  fi

  if [ -d "$MARATHON_DIR/.git" ]; then
    echo "=> marathon is already installed in $MARATHON_DIR, trying to update"
    printf "\r=> "
    cd "$MARATHON_DIR" && (git fetch 2> /dev/null || {
      echo >&2 "Failed to update marathon, run 'git fetch' in $MARATHON_DIR yourself." && exit 1
    })
  else
    # Cloning to $MARATHON_DIR
    echo "=> Downloading marathon from git to '$MARATHON_DIR'"
    printf "\r=> "
    mkdir -p "$MARATHON_DIR"
    git clone "$MARATHON_SOURCE" "$MARATHON_DIR"
  fi
  cd $MARATHON_DIR && git checkout master || true
}

install_marathon_from_git

# Detect profile file if not specified as environment variable (eg: PROFILE=~/.myprofile).
if [ -z "$PROFILE" ]; then
  if [ -f "$HOME/.zshrc" ]; then
    PROFILE="$HOME/.zshrc"
  elif [ -f "$HOME/.bashrc" ]; then
    PROFILE="$HOME/.bashrc"
  elif [ -f "$HOME/.bash_profile" ]; then
    PROFILE="$HOME/.bash_profile"
  elif [ -f "$HOME/.profile" ]; then
    PROFILE="$HOME/.profile"
  fi
fi


SOURCE_STR="\nexport MARATHON_DIR=\"$MARATHON_DIR\"\n[ -s \"\$MARATHON_DIR/marathon.sh\" ] && . \"\$MARATHON_DIR/marathon.sh\"  # This loads marathon"

if [ -z "$PROFILE" ] || [ ! -f "$PROFILE" ] ; then
  if [ -z "$PROFILE" ]; then
    echo "=> Profile not found. Tried ~/.bashrc, ~/.bash_profile, ~/.zshrc, and ~/.profile."
    echo "=> Create one of them and run this script again"
  else
    echo "=> Profile $PROFILE not found"
    echo "=> Create it (touch $PROFILE) and run this script again"
  fi
  echo "   OR"
  echo "=> Append the following lines to the correct file yourself:"
  printf "$SOURCE_STR"
  echo
else
  if ! grep -qc 'marathon.sh' "$PROFILE"; then
    echo "=> Appending source string to $PROFILE"
    printf "$SOURCE_STR\n" >> "$PROFILE"
  else
    echo "=> Source string already in $PROFILE"
  fi
fi

echo "=> Close and reopen your terminal to start using marathon"