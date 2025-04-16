// Add placementLoginStatus LiveData
private val _placementLoginStatus = MutableLiveData<Resource<Boolean>>()
val placementLoginStatus: LiveData<Resource<Boolean>> = _placementLoginStatus

// Add loginPlacementCell function
fun loginPlacementCell(email: String, password: String) {
    _placementLoginStatus.value = Resource.Loading()
    viewModelScope.launch {
        try {
            val placementUser = userRepository.loginPlacementCell(email, password)
            if (placementUser != null) {
                sessionManager.saveUserDetails(
                    placementUser.id,
                    placementUser.name,
                    placementUser.email,
                    UserRole.PLACEMENT_CELL
                )
                _placementLoginStatus.value = Resource.Success(true)
            } else {
                _placementLoginStatus.value = Resource.Error("Invalid credentials")
            }
        } catch (e: Exception) {
            _placementLoginStatus.value = Resource.Error(e.message ?: "Login failed")
        }
    }
} 