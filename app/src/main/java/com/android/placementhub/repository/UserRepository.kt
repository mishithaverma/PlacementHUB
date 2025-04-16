import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Add loginPlacementCell function to authenticate placement cell users
suspend fun loginPlacementCell(email: String, password: String): User? {
    return withContext(Dispatchers.IO) {
        try {
            val placementUser = userDao.getUserByEmailAndRole(email, UserRole.PLACEMENT_CELL.name)
            
            if (placementUser != null && passwordEncoder.matches(password, placementUser.password)) {
                return@withContext placementUser
            }
            return@withContext null
        } catch (e: Exception) {
            throw e
        }
    }
} 