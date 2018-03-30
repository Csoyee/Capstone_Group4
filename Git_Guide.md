# How to use github


[참고 링크](https://github.com/KennethanCeyer/tutorial-git)


### Status
- 파일이 변경 되었는지 변경된 후에 add가 되었는지를 알 수 있다. (직접 해봐야 알 수 있어요!)

$ git status


### Clone 
- 원격 저장소에 있는 코드를 받아오는 과정 
- 아래의 clone을 수행하면 Capstone_Group4 폴더가 생긴다!

$ git clone "https://github.com/Csoyee/Capstone_Group4"


### Commit / Push
- 수정한 코드를 업로드 하는 과정
- add, commit, Push의 순서로 이루어진다.
- 이 때, 내가 git에 push를 하기 전에 다른 사람이 push를 할 경우에 버전 문제로 push 가 안될 수 있다. 이 때 git pull을 통해 변경 사항을 받아와야 한다.

$ git add *

$ git commit -m "commit message"

$ git push


### Pull
- 내가 git에 push를 하기 전에 다른 사람이 push를 할 경우에 push가 안됨. 이는 어떤 코드가 최신이고 맞는지 github가 알기 어려워 코드 conflict가 발생하기 때문
- 원격 저장소에서 최신 버전의 코드를 pull 한 후 conflict 가 난 부분을 찾아서 해결한 뒤에 다시 Commit / Push 과정을 수행해야 한다.

$ git pull


### Branch
- 개발 중인 코드가 섞이기 때문에 branch를 나누어서 작업하는 경우가 있다.

- 브랜치 생성

$ git checkout -b new_branch_name

- 해당 브랜치로 push

$ git push origin branch_name

- 브랜치 삭제

$ git checkout master

$ git branch -d branch_name

$ git push origin :branch_name


### merge / Conflict
- 다른 branch와 master branch를 합할 때 필요한 내용

$ git checkout -f master

$ git merge [다른 branch]   # 현재 브랜치 master, master 에서 다른 브렌치를 머지. (다른 브린치 내용이 마스터에 적용)

$ git push origin master    # 까지 해줘야 원격 git에 적용됨.
